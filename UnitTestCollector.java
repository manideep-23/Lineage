package com.yourplugin.sparklineageplugin;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.HashSet;
import java.util.Set;
public class UnitTestCollector {

    public String collectFullClassContext(PsiClass psiClass) {
        StringBuilder fullCode = new StringBuilder();



         PsiFile containingFile = psiClass.getContainingFile();
        if (containingFile instanceof PsiJavaFile) {
            fullCode.append(collectImports((PsiJavaFile) containingFile)).append("\n");
        }

        // Class declaration line
        fullCode.append(psiClass.getModifierList() != null ? psiClass.getModifierList().getText() + " " : "")
                .append("class ").append(psiClass.getName()).append(" {\n\n");

        // Fields with values
        fullCode.append(collectFieldValues(psiClass));



        // Collect all public methods of the class
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.hasModifierProperty(PsiModifier.PUBLIC)) {
                fullCode.append(collectFullMethodContext(method));
            }
        }

        // Add class-level fields with values
        fullCode.append(collectFieldValues(psiClass));

        return fullCode.toString();
    }

    public String collectFullMethodContext(PsiMethod method) {
        StringBuilder context = new StringBuilder();

        // Current method
        context.append(method.getText()).append("\n\n");

        // Internal private methods of same class
        Set<PsiMethod> privateMethods = findCalledPrivateMethods(method, method.getContainingClass());
        for (PsiMethod privateMethod : privateMethods) {
            context.append("// Private method in same class\n");
            context.append(privateMethod.getText()).append("\n\n");
        }

        // Private methods of other classes called from here
        Set<PsiMethod> externalPrivateMethods = findExternalPrivateMethods(method);
        for (PsiMethod privateMethod : externalPrivateMethods) {
            context.append("// Private method from another class\n");
            context.append(privateMethod.getText()).append("\n\n");
            context.append(collectFieldValues(privateMethod.getContainingClass())); // Fields from dependent class
        }

        // Fields from current class
        context.append(collectFieldValues(method.getContainingClass()));

        return context.toString();
    }

    private Set<PsiMethod> findCalledPrivateMethods(PsiMethod method, PsiClass sameClass) {
        Set<PsiMethod> privateMethods = new HashSet<>();
        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                PsiMethod resolvedMethod = expression.resolveMethod();
                if (resolvedMethod != null
                        && resolvedMethod.hasModifierProperty(PsiModifier.PRIVATE)
                        && resolvedMethod.getContainingClass().equals(sameClass)) {
                    privateMethods.add(resolvedMethod);
                    privateMethods.addAll(findCalledPrivateMethods(resolvedMethod, sameClass)); // recursive
                }
            }
        });
        return privateMethods;
    }

    private Set<PsiMethod> findExternalPrivateMethods(PsiMethod method) {
        Set<PsiMethod> privateMethods = new HashSet<>();
        method.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                PsiMethod resolvedMethod = expression.resolveMethod();
                if (resolvedMethod != null
                        && resolvedMethod.hasModifierProperty(PsiModifier.PRIVATE)
                        && !resolvedMethod.getContainingClass().equals(method.getContainingClass())) {
                    privateMethods.add(resolvedMethod);
                }
            }
        });
        return privateMethods;
    }

    private String collectFieldValues(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("// Fields from class ").append(psiClass.getName()).append("\n");
        for (PsiField field : psiClass.getAllFields()) {
            PsiExpression initializer = field.getInitializer();
            if (initializer != null) {
                sb.append(field.getType().getPresentableText())
                        .append(" ")
                        .append(field.getName())
                        .append(" = ")
                        .append(initializer.getText())
                        .append(";\n");
            }
        }
        sb.append("\n");
        return sb.toString();
    }

  /*  private String collectImports(PsiJavaFile javaFile) {
        StringBuilder sb = new StringBuilder();
        for (PsiImportStatement importStmt : javaFile.getImportList().getAllImportStatements()) {
            sb.append(importStmt.getText()).append("\n");
        }
        return sb.toString();
    }*/
  private String collectImports(PsiJavaFile javaFile) {
      StringBuilder sb = new StringBuilder();
      for (PsiImportStatementBase importStmt : javaFile.getImportList().getAllImportStatements()) {
          sb.append(importStmt.getText()).append("\n");
      }
      return sb.toString();
  }

}
