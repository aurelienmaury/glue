package org.glue

import org.glue.exception.UnacceptableProjectTreeException


class Main {

    public static void main(String[] args) {

        String mainJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().path
        String glueHomePath = new File(mainJarPath).parentFile.parentFile.absolutePath

        CliBuilder cli = new CliBuilder(usage: "glue - static websites micro-builder")

        cli.with {
            h(longOpt: 'help', 'Show usage information')
            c(longOpt: 'create', args: 1, 'generate new Glue project structure')
        }

        def options = cli.parse(args)

        if (!options) {
            System.exit(0)
        }

        if (options.h) {
            cli.usage()
            System.exit(0)
        }

        if (options.c) {
            createProject(glueHomePath, options.c)
        } else {
            assembleProject()
        }
    }


    static void createProject(String glueHomePath, String projectPath) {
        ProjectManager projectManager = new ProjectManager()

        try {
            projectManager.generateDirTree(glueHomePath, 'default', projectPath)
            projectManager.checkDirTree(projectPath)
        } catch (UnacceptableProjectTreeException e) {
            println('Error: ' + e.message)
            System.exit(1)
        }
    }

    static void assembleProject() {
        AssetManager assetManager = new AssetManager(ProjectManager.ASSETS_PATH, ProjectManager.OUTPUT_PATH)
        assetManager.start()

        TemplateManager templateManager = new TemplateManager(ProjectManager.PAGES_PATH, ProjectManager.LAYOUTS_PATH, ProjectManager.OUTPUT_PATH)
        templateManager.start()

        println "Glued"
    }
}
