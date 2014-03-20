package org.glue

import org.glue.exception.UnacceptableProjectTreeException


class Main {

    public static void main(String[] args) {

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

            ProjectManager projectManager = new ProjectManager()

            try {
                projectManager.generateDirTree(options.c)
                projectManager.checkDirTree(options.c)
            } catch (UnacceptableProjectTreeException e) {
                println('Error: ' + e.message)
                System.exit(1)
            }
        } else {

            AssetManager assetManager = new AssetManager(ProjectManager.ASSETS_PATH, ProjectManager.OUTPUT_PATH)
            assetManager.start()

            TemplateManager templateManager = new TemplateManager(ProjectManager.PAGES_PATH, ProjectManager.LAYOUTS_PATH, ProjectManager.OUTPUT_PATH)
            templateManager.start()

            println "Glued !!!"
        }
    }
}
