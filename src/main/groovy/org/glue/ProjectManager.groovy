package org.glue

import groovy.transform.CompileStatic
import org.glue.exception.UnacceptableProjectTreeException

import static groovy.io.FileType.DIRECTORIES
import static groovy.io.FileType.FILES

@CompileStatic
class ProjectManager {

    static final String OUTPUT_PATH = 'target'
    static final String ASSETS_PATH = 'assets'
    static final String LAYOUTS_PATH = 'layouts'
    static final String PAGES_PATH = 'pages'

    void checkDirTree(String targetDirPath) {
        File targetDir = openTargetDir(targetDirPath)

        List<String> targetSubDirNameList = targetDir.listFiles(
                [accept: { File file -> file.isDirectory() }] as FileFilter
        ).toList()*.name

        if (!targetSubDirNameList.containsAll(ASSETS_PATH, LAYOUTS_PATH, PAGES_PATH)) {
            throw new UnacceptableProjectTreeException('Glue project misses at least one of these directories: ' + [ASSETS_PATH, LAYOUTS_PATH, PAGES_PATH])
        }
    }

    void generateDirTree(String glueHomePath, String projectTemplateName, String targetDirPath) {

        File srcDir = new File(glueHomePath + File.separator + 'project-templates' + File.separator + projectTemplateName)
        File targetDir = createTargetDir(targetDirPath)

        srcDir.eachFileRecurse(DIRECTORIES) { File templateSubDir ->
            if (templateSubDir.listFiles()) {
                String relativePath = templateSubDir.absolutePath - srcDir.absolutePath
                new File(targetDir.absolutePath + relativePath).mkdirs()
            }
        }

        srcDir.eachFileRecurse(FILES) { File assetFile ->

            String relativePath = assetFile.absolutePath - srcDir.absolutePath
            String destFilePath = targetDir.absolutePath + relativePath
            new File(destFilePath).withOutputStream { OutputStream output ->
                assetFile.withInputStream { InputStream input ->
                    output << input
                }
            }
        }
    }


    private File createTargetDir(String targetDirPath) {
        def targetDir = new File(targetDirPath)

        if (targetDir.exists() && targetDir.isFile()) {
            throw new UnacceptableProjectTreeException('Attempted to create directory but file exists with same name: ' + targetDirPath)
        }

        targetDir.mkdir()

        targetDir
    }

    private File openTargetDir(String targetDirPath) {
        def targetDir = new File(targetDirPath)
        if (!targetDir.exists()) {
            throw new UnacceptableProjectTreeException('Glue project does not exist: ' + targetDirPath)
        }

        if (!targetDir.isDirectory()) {
            throw new UnacceptableProjectTreeException('Glue project is not a directory: ' + targetDirPath)
        }
        targetDir
    }
}
