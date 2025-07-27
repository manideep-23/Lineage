package com.yourplugin.sparklineageplugin;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
public class UnitTestCollector {


    public String collectFullClassDependencyContext(PsiClass psiClass,Project project) {
        StringBuilder fullCode = new StringBuilder();

        // Collect all public methods of the class
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.hasModifierProperty(PsiModifier.PUBLIC)) {

               String code= new SparkCodeCollector().collectFullMethodContext(method)+"\n";
                fullCode.append(code);
            }
        }

        // Add class-level fields with values
        //   fullCode.append(collectFieldValues(psiClass));

        return fullCode.toString();
    }



    public String collectFullClassContext(PsiClass psiClass,Project project) {
        StringBuilder fullCode = new StringBuilder();



         PsiFile containingFile = psiClass.getContainingFile();
        if (containingFile instanceof PsiJavaFile) {
            fullCode.append(collectImports((PsiJavaFile) containingFile)).append("\n");
        }
        Set<String> missingImports = collectMissingImports(psiClass,project);
        for (String imp : missingImports) {
            System.out.println("Missing imports : ");
            fullCode.append("import ").append(imp).append(";\n");
        }

        // Class declaration line
        fullCode.append(psiClass.getModifierList() != null ? psiClass.getModifierList().getText() + " " : "")
                .append("class ").append(psiClass.getName()).append(" {\n\n");

        // Fields with values
        fullCode.append(collectFieldValues(psiClass));



        // Collect all public methods of the class
        for (PsiMethod method : psiClass.getMethods()) {
            if (method.hasModifierProperty(PsiModifier.PUBLIC)) {
                fullCode.append(collectFullMethodContext(psiClass,method,project,true));
            }
        }

        // Add class-level fields with values
     //   fullCode.append(collectFieldValues(psiClass));

        return fullCode.toString();
    }

    public String collectFullMethodContext(PsiClass psiClass,PsiMethod method,Project project,boolean isImportAdded) {
        StringBuilder context = new StringBuilder();

        System.out.println("isImportAdded : "+isImportAdded);
        if(!isImportAdded)
        {
            System.out.println("adding importsand variables");
            PsiFile containingFile = psiClass.getContainingFile();
            if (containingFile instanceof PsiJavaFile) {
                context.append(collectImports((PsiJavaFile) containingFile)).append("\n");
            }
            Set<String> missingImports = collectMissingImports(psiClass,project);
            for (String imp : missingImports) {
                context.append("import ").append(imp).append(";\n");
            }

            // Class declaration line
            context.append(psiClass.getModifierList() != null ? psiClass.getModifierList().getText() + " " : "")
                    .append("class ").append(psiClass.getName()).append(" {\n\n");

            // Fields with values
            context.append(collectFieldValues(psiClass));
        }

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
       // context.append(collectFieldValues(method.getContainingClass()));

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

    private boolean isAlreadyImported(String className, PsiJavaFile psiFile) {
        return Arrays.stream(psiFile.getImportList().getImportStatements())
                .anyMatch(importStmt -> importStmt.getQualifiedName().endsWith("." + className));
    }
    private boolean isInSamePackage(String className, PsiJavaFile psiFile,Project project) {
        String currentPkg = psiFile.getPackageName();
        PsiClass[] classes = JavaPsiFacade.getInstance(project)
                .findClasses(className, GlobalSearchScope.projectScope(project));
        for (PsiClass cls : classes) {
            PsiFile file = cls.getContainingFile();
            if (file instanceof PsiJavaFile) {
                String otherPkg = ((PsiJavaFile) file).getPackageName();
                if (currentPkg.equals(otherPkg)) return true;
            }
        }
        return false;
    }

    private String resolveQualifiedName(String className, PsiClass contextClass) {
        Project project = contextClass.getProject();
       /* PsiClass[] classes = JavaPsiFacade.getInstance(project)
                .findClasses(className, GlobalSearchScope.projectScope(project));
*/
        PsiClass[] classes = PsiShortNamesCache.getInstance(project)
                .getClassesByName(className, GlobalSearchScope.projectScope(project));

        System.out.println(className +"classes : "+classes);
        if(classes.length>0)
        System.out.println(className+ "classes [0] : "+classes[0]);
        if (classes.length==0)
            System.out.println(className+"length 0 ");

        if (classes.length == 0) return null; // Not found
        PsiClass chosen = classes[0]; // randomly pick one
       /* return JavaPsiFacade.getInstance(project).findClass(className, GlobalSearchScope.projectScope(project))
                .getQualifiedName();*/
        return chosen.getQualifiedName();
    }

    private boolean isUserDefinedType(PsiType type) {
        String qualifiedName = type.getCanonicalText();
        return !(qualifiedName.startsWith("java.") || qualifiedName.startsWith("javax.") || qualifiedName.contains("<"));
    }

    private Set<String> collectMissingImports(PsiClass psiClass,Project project) {
        Set<String> imports = new HashSet<>();
        PsiFile file = psiClass.getContainingFile();
        if (!(file instanceof PsiJavaFile)) return imports;

        PsiJavaFile javaFile = (PsiJavaFile) file;

        for (PsiField field : psiClass.getAllFields()) {
            PsiType type = field.getType();
            System.out.println("type : "+type.getCanonicalText()+" !isUserDefinedType(type) "+!isUserDefinedType(type));
            if (!isUserDefinedType(type)) continue;

            String className = type.getPresentableText();


            if (isAlreadyImported(className, javaFile)
                    //|| isInSamePackage(className, javaFile,project)
            ) {
                continue;
            }
            System.out.println("collectMissingImports : "+className);

            String qualifiedName = resolveQualifiedName(className, psiClass);
            if (qualifiedName != null) imports.add(qualifiedName);
        }

        return imports;
    }





}
