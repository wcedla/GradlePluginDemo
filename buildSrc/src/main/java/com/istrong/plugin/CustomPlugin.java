package com.istrong.plugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CustomPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        if (!project.getPlugins().hasPlugin(AppPlugin.class)) {
            throw new GradleException("请在app模块中的build.gradle中引用本插件:CustomPlugin");
        }
        //获取android extension
        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        //注册自己写的Transform
        appExtension.registerTransform(new CustomTransform(project));
    }
}
