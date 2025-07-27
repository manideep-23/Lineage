package com.yourplugin.sparklineageplugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.ide.highlighter.JavaFileType;

import com.yourplugin.sparklineageplugin.authentication.TokenStorageService;
import com.yourplugin.sparklineageplugin.prompts.DynamicTestPromptGenerator;
import com.yourplugin.sparklineageplugin.settings.UnitTestSettingsState;
import com.yourplugin.sparklineageplugin.ui.UnitTestSettingsDialog;
import org.jetbrains.annotations.NotNull;

public class LineageAction extends AnAction {
   /* @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);

        String fullCode = null;

        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if (method == null) {
            Messages.showErrorDialog("Please place the cursor inside a method.", "No Method Found");
            return;
        }
        fullCode = new SparkCodeCollector().collectFullMethodContext(method);

        if (fullCode == null || fullCode.isEmpty()) {
            Messages.showErrorDialog("Could not collect method context.", "Collection Failed");
            return;
        }

        String prompt = PromptBuilder.buildPrompt(fullCode);
        String result = LLMClient.sendPrompt(prompt);
     //   System.out.println(LLMClient.getClaude3Response(prompt));
        System.out.println("first result is : "+ result);
   *//*     String secondPrompt = "This was the previous response:\n\n" + result +
                "\n\n Mermaid should contain **all the column info** as well. \n" +
                "Give me detailed columns of each dataset** and **mappings till the final dataset for all. \n"+
                "Ensure all quotes are escaped, all column mappings are present, and syntax is correct for mermaid.";
       // result=LLMClient.sendPrompt(secondPrompt);*//*
        //System.out.println("second result is : "+ result);
      LineageResultPanel.show(project, result);
       // LineageResultPanelEnhanced2.show(project, result);
    }*/
/*

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            return;
        }

        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        PsiMethod method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);

        if (method == null) {
            Messages.showErrorDialog("Please place the cursor inside a method.", "No Method Found");
            return;
        }

        String fullCode = new SparkCodeCollector().collectFullMethodContext(method);

        if (fullCode == null || fullCode.isEmpty()) {
            Messages.showErrorDialog("Could not collect method context.", "Collection Failed");
            return;
        }

        String prompt = PromptBuilder.buildPrompt(fullCode);

        // Show loading dialog while LLM processes the prompt
        ProgressManager.getInstance().run(new Task.Modal(project, "Generating Spark Lineage...", true) {
            private String result;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Contacting LLM to analyze Spark method...");
                result = LLMClient.sendPrompt(prompt); // long-running LLM call
            }

            @Override
            public void onSuccess() {
                if (result != null && !result.isEmpty()) {
                    LineageResultPanel.show(project, result);
                } else {
                    Messages.showErrorDialog("Received empty result from LLM.", "Lineage Generation Failed");
                }
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error while generating lineage: " + error.getMessage(), "Lineage Error");
            }
        });
    }
*/



    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);

        if (project == null || editor == null || psiFile == null) {
            return;
        }

        PsiElement element = null;
        PsiMethod method = null;
        PsiClass psiClass = null;
        String fullCode = null;
        String dependentCode=null;

        if (editor != null) {
            int offset = editor.getCaretModel().getOffset();
            element = psiFile.findElementAt(offset);
            method = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
            psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        }else {
            element = e.getData(CommonDataKeys.PSI_ELEMENT);
            if (element instanceof PsiClass) {
                psiClass = (PsiClass) element;
            }
        }



        String actionContext=e.getPresentation().getText();
        System.out.println("actionContext : "+actionContext);

        TokenStorageService t=new TokenStorageService();
        t.saveToken(t.generateFormattedToken());

        if(actionContext.equalsIgnoreCase("Generate Test")) {

            System.out.println("Inside Generate Test");
            if (psiFile instanceof PsiJavaFile && project != null) {

                 psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);

                //  PsiClass[] classes = ((PsiJavaFile) file).getClasses();
                //if (classes.length > 0) {
                //PsiClass targetClass = classes[0];
                // String classCode = targetClass.getText();

                UnitTestSettingsDialog dialog = new UnitTestSettingsDialog();
                if (!dialog.showAndGet()) return;

               UnitTestSettingsState settings = dialog.getSettings();
               // String fullCode = new SparkCodeCollector().collectFullMethodContext(method);

                fullCode = (psiClass != null && method == null) ? new UnitTestCollector().collectFullClassContext(psiClass,project)
                        : new UnitTestCollector().collectFullMethodContext(psiClass,method,project,false);

                dependentCode=(psiClass != null && method == null) ?
                      new UnitTestCollector().collectFullClassDependencyContext(psiClass,project)
            : new SparkCodeCollector().collectFullMethodContext(method);

               // System.out.println("dependentCode : "+dependentCode);

                if (fullCode == null || fullCode.isEmpty()) {
                    Messages.showErrorDialog("Could not collect method context.", "Collection Failed");
                    return;
                }



                String className = psiClass.getName();
                String testClassName = className + "Test";
                String packageName = ((PsiJavaFile) psiFile).getPackageName();
               /* String promptBuilder = PromptBuilder.getPrompt(settings.javaVersion,
                        settings.sparkVersion, settings.mockitoVersion, settings.language,
                        settings.framework, fullCode, className, testClassName, packageName);
*/

                DynamicTestPromptGenerator.TestConfig  testConfig=new DynamicTestPromptGenerator.
                        TestConfig(settings.language,settings.languageVersion,
                        settings.framework,settings.sparkVersion
                        ,settings.springBootVersion,settings.testFramework,
                        settings.mockitoVersion, settings.testFrameworkVersion,packageName,testClassName,fullCode,dependentCode);

                String promptBuilderGeneric=DynamicTestPromptGenerator.generatePrompt(testConfig);
                System.out.println("promptBuilderGeneric : "+promptBuilderGeneric);
                String result = LLMClient.sendPrompt(promptBuilderGeneric);
                System.out.println(result);
                String TestCodeGenerated=CodeExtractor.extractJavaCode(result);
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    String path = TestFileWriter2.writeTestFile(project, psiFile, packageName, testClassName, TestCodeGenerated);
                    Messages.showInfoMessage("JUnit Java file created at location:\n" + path, "Unit Test Generated");
                });

                return;
                //}
            }
        }

        if (method == null) {
            Messages.showErrorDialog("Please place the cursor on a method and run", "No Method Found");
            return;
        }
         fullCode = new SparkCodeCollector().collectFullMethodContext(method);

        if (fullCode == null || fullCode.isEmpty()) {
            Messages.showErrorDialog("Could not collect method context.", "Collection Failed");
            return;
        }

        String prompt = PromptBuilder.buildPrompt(fullCode);

        ProgressManager.getInstance().run(new Task.Modal(project, "Generating Spark Lineage...", true) {
            private String result = null;
            private boolean wasCancelled = false;
            private Thread workerThread;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                indicator.setText("Calling LLM...");

                workerThread = new Thread(() -> {
                    try {
                        result = LLMClient.sendPrompt(prompt);
                    } catch (Exception ex) {
                        // capture exception if needed
                        result = null;
                    }
                });

                workerThread.start();

                // Wait for the thread, while checking for cancellation
                while (workerThread.isAlive()) {
                    if (indicator.isCanceled()) {
                        wasCancelled = true;
                        workerThread.interrupt();  // this works only if LLMClient honors interruption
                        break;
                    }
                    try {
                        Thread.sleep(200); // check every 200ms
                    } catch (InterruptedException ignored) {}
                }
            }

            @Override
            public void onSuccess() {
                if (wasCancelled) {
                    Messages.showInfoMessage("Lineage generation was cancelled by the user.", "Cancelled");
                    return;
                }

                if (result != null && !result.isEmpty()) {
                    LineageVisualizerPanel.show(project, result);
                } else {
                    Messages.showErrorDialog("Failed to get result from LLM.", "Lineage Generation Failed");
                }
            }

            @Override
            public void onThrowable(@NotNull Throwable error) {
                Messages.showErrorDialog("Error: " + error.getMessage(), "Lineage Generation Error");
            }
        });
    }


 /*  @Override
    public void update(AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        e.getPresentation().setEnabledAndVisible(element instanceof PsiMethod);
    }
*/

    @Override
    public void update(AnActionEvent e) {
        PsiElement element = e.getData(CommonDataKeys.PSI_ELEMENT);
        boolean isEnabled = element instanceof PsiMethod || element instanceof PsiClass;
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }
}
