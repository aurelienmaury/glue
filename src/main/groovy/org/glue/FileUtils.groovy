package org.glue

import groovy.transform.CompileStatic

import static groovy.io.FileType.DIRECTORIES

@CompileStatic
class FileUtils {
    static void copyNonEmptySubDirectories(String srcPath, String destPath) {
        File srcDir = new File(srcPath)
        File destDir = new File(destPath)

        srcDir.eachFileRecurse(DIRECTORIES) { File srcSubDir ->
            if (srcSubDir.listFiles()) {
                String relativePath = srcSubDir.absolutePath - srcDir.absolutePath
                new File(destDir.absolutePath + relativePath).mkdirs()
                println "\tCreated ${destDir.name}${relativePath}"
            }
        }
    }
}
