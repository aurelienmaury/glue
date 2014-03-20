package org.glue

import com.github.sommeri.less4j.core.DefaultLessCompiler
import groovy.transform.CompileStatic

import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

@CompileStatic
class AssetManager {

    String assetsSrcPath
    File assetsSrcDir

    String assetsDestPath
    File assetsDestDir

    AssetManager(String assetsSrcPath, String assetsDestPath) {
        this.assetsSrcPath = assetsSrcPath
        this.assetsSrcDir = new File(this.assetsSrcPath)

        this.assetsDestPath = assetsDestPath
        this.assetsDestDir = new File(this.assetsDestPath)
    }

    void start() {
        if (!assetsDestDir.exists() || !assetsDestDir.isDirectory()) {
            assetsDestDir.mkdir()
        }

        mimicAssetsDirTree()
        copyAssets()
        compileLessCss()
    }

    private void compileLessCss() {

        DefaultLessCompiler lessCompiler = new DefaultLessCompiler()
        assetsSrcDir.eachFileRecurse(FILES) { File lessCssFile ->
            if (lessCssFile.name.endsWith('.less')) {
                String relativePath = lessCssFile.absolutePath - assetsSrcDir.absolutePath
                def destFile = new File((assetsDestDir.absolutePath + relativePath) - '.less' + '.css')
                destFile.write(lessCompiler.compile(lessCssFile).css)
            }
        }
    }

    private void mimicAssetsDirTree() {

        assetsSrcDir.eachFileRecurse(DIRECTORIES) { File assetSubDir ->
            if (assetSubDir.listFiles()) {
                String relativePath = assetSubDir.absolutePath - assetsSrcDir.absolutePath
                new File(assetsDestDir.absolutePath + relativePath).mkdirs()
            }
        }
    }

    private void copyAssets() {

        assetsSrcDir.eachFileRecurse(FILES) { File assetFile ->
            if (!assetFile.name.endsWith('.less')) {

                String relativePath = assetFile.absolutePath - assetsSrcDir.absolutePath
                String destFilePath = assetsDestDir.absolutePath + relativePath
                new File(destFilePath).withOutputStream { OutputStream output ->
                    assetFile.withInputStream { InputStream input ->
                        output << input
                    }
                }
            }
        }
    }
}
