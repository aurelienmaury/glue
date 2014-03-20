package org.glue

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import groovy.transform.CompileStatic

import static groovy.io.FileType.FILES

@CompileStatic
class TemplateManager {

    String templatesSrcPath
    File templatesSrcDir

    String templatesDestPath
    File templatesDestDir

    String layoutsSrcPath
    File layoutsSrcDir

    TemplateManager(String templatesSrcPath, String layoutsSrcPath, String templatesDestPath) {
        this.templatesSrcPath = templatesSrcPath
        this.templatesSrcDir = new File(this.templatesSrcPath)

        this.layoutsSrcPath = layoutsSrcPath
        this.layoutsSrcDir = new File(this.layoutsSrcPath)

        this.templatesDestPath = templatesDestPath
        this.templatesDestDir = new File(this.templatesDestPath)
    }

    void start() {
        if (!templatesDestDir.exists() || !templatesDestDir.isDirectory()) {
            templatesDestDir.mkdir()
        }

        compileMustachePages()
    }

    private  void compileMustachePages() {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory(layoutsSrcDir)

        templatesSrcDir.eachFileRecurse(FILES) { File pageFile ->
            if (pageFile.name.endsWith('.html')) {
                try {
                    String relativePath = pageFile.absolutePath - templatesSrcDir.absolutePath
                    def destFile = new File(templatesDestDir.absolutePath + relativePath)
                    Mustache mustache = mustacheFactory.compile(pageFile.newReader(), relativePath)
                    destFile.withWriter { Writer writer ->
                        mustache.execute(writer, [:])
                    }
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }
    }
}
