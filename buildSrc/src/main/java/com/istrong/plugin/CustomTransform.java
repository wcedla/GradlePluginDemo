package com.istrong.plugin;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class CustomTransform extends Transform {

    private Project project;

    public CustomTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "自定义Transform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    ClassPool classPool = ClassPool.getDefault();

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider transformOutputProvider = transformInvocation.getOutputProvider();
        inputs.forEach(input -> {
            //文件夹类型
            input.getDirectoryInputs().forEach(directoryInput -> {
                try {
//                    System.out.println("文件夹类型输入的路径:" + directoryInput.getFile().getPath());
                    File outPutLocation = transformOutputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
//                    System.out.println("文件夹类型输出路径:" + outPutLocation);
                    File file = directoryInput.getFile();
                    String filePath = file.getAbsolutePath();
                    classPool.appendClassPath(filePath);
                    injectMethod(filePath);

                    FileUtils.copyDirectory(directoryInput.getFile(), outPutLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            //jar类型
            input.getJarInputs().forEach(jarInput -> {
                try {
//                System.out.println("jar类型输入路径:" + jarInput.getFile().getPath());
                    File outPutLocation = transformOutputProvider.getContentLocation(jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
//                System.out.println("jar类型输出路径:" + outPutLocation);
                    FileUtils.copyFile(jarInput.getFile(), outPutLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void injectMethod(String rootPath) {
        File directoryFolder = new File(rootPath);
        for (File allFile : FileUtils.getAllFiles(directoryFolder)) {
            String filePath = allFile.getAbsolutePath();
            System.out.println("类文件地址:" + filePath);
            if (filePath.endsWith(".class") && !filePath.contains("R$") && !filePath.contains("R.class") && !filePath.contains("BuildConfig.class")) {
                if (filePath.contains("com" + File.separator + "istrong" + File.separator + "gradleplugindemo")) {
                    System.out.println("成功获取路径:" + filePath);
                    try {
                        CtClass ctClass = ClassPool.getDefault().get("com.istrong.gradleplugindemo.MainActivity");
                        if (ctClass.isFrozen()) {
                            ctClass.defrost();
                        }
                        CtMethod ctMethod = ctClass.getDeclaredMethod("clickAction");
                        ctMethod.insertBefore("System.out.println(\"点击事件我改了啊\");");
                        ctClass.writeFile(rootPath);
                        ctClass.detach();
//                        CtMethod ctMethod = ctClass.getDeclaredMethod("clickAction");
//                        ctMethod.setBody("System.out.println(\"点击事件我改了啊\");");
//                        ctClass.writeFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
