package org.glue

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.github.mustachejava.MustacheFactory
import com.github.sommeri.less4j.core.DefaultLessCompiler
import groovy.transform.CompileStatic

import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

@CompileStatic
class Main {

    static final String OUTPUT_PATH = 'output'
    static final String ASSETS_PATH = 'assets'
    static final String PAGES_PATH = 'pages'

    public static void main(String[] args) {

        File outputDir = getOutputDir()

        mimiAssetsDirTreeTo(outputDir)
        copyAssetsTo(outputDir)
        compileLessCssTo(outputDir)
        compileMustachePagesTo(outputDir)

        println "Glued !!!"
    }

    private static void compileMustachePagesTo(File outputDir) {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory(new File('layouts'))

        def pagesDir = new File(PAGES_PATH)
        pagesDir.eachFileRecurse(FILES) { File pageFile ->
            if (pageFile.name.endsWith('.html')) {
                try {
                    String relativePath = pageFile.absolutePath - pagesDir.absolutePath
                    def destFile = new File(outputDir.absolutePath + relativePath)
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

    private static void compileLessCssTo(File outputDir) {
        def assetsDir = new File(ASSETS_PATH)
        DefaultLessCompiler lessCompiler = new DefaultLessCompiler()
        assetsDir.eachFileRecurse(FILES) { File lessCssFile ->
            if (lessCssFile.name.endsWith('.less')) {
                String relativePath = lessCssFile.absolutePath - assetsDir.absolutePath
                def destFile = new File((outputDir.absolutePath + relativePath) - '.less' + '.css')
                destFile.write(lessCompiler.compile(lessCssFile).css)
            }
        }
    }

    private static void mimiAssetsDirTreeTo(File outputDir) {
        def assetsDir = new File(ASSETS_PATH)
        assetsDir.eachFileRecurse(DIRECTORIES) { File assetSubDir ->
            if (assetSubDir.listFiles()) {
                String relativePath = assetSubDir.absolutePath - assetsDir.absolutePath
                new File(outputDir.absolutePath + relativePath).mkdirs()
            }
        }
    }

    private static void copyAssetsTo(File outputDir) {
        def assetsDir = new File(ASSETS_PATH)
        assetsDir.eachFileRecurse(FILES) { File assetFile ->
            if (!assetFile.name.endsWith('.less')) {

                String relativePath = assetFile.absolutePath - assetsDir.absolutePath
                String destFilePath = outputDir.absolutePath + relativePath
                new File(destFilePath).withOutputStream { OutputStream output ->
                    assetFile.withInputStream { InputStream input ->
                        output << input
                    }
                }
            }
        }
    }

    private static File getOutputDir() {
        def outputDir = new File(OUTPUT_PATH)
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            outputDir.mkdir()
        }
        outputDir
    }
}
