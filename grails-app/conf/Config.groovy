// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// -server -Xms512M -Xmx512M

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text/plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.logging.jul.usebridge = true
        // TODO: grails.serverURL = "http://www.changeme.com"
        //      grails.serverURL = "http://77.43.32.198:8080/${appName}"
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
    }

}

// log4j configuration
/*
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n')

        switch (grails.util.Environment.current) {
            case grails.util.Environment.PRODUCTION:
                //file name: 'bioAppender', file: '/Users/soffitta/logs/webbio.log', maxFileSize: 20000
                file name: 'bioAppender', file: '/logs/newwebbio.log', maxFileSize: 20000

                def patternLayout = new org.apache.log4j.PatternLayout()
                patternLayout.setConversionPattern("[%r] %c{2} %m%n")
                def mailAppender = new org.apache.log4j.net.SMTPAppender()
                mailAppender.setFrom("gac@algos.it")
                mailAppender.setTo("gac@algos.it")
                mailAppender.setSubject("Errore nel programma Biobot")
                mailAppender.setSMTPHost("smtp.algos.it")
                mailAppender.setSMTPUsername('gac@algos.it')
                mailAppender.setSMTPPassword('fulvia')
                mailAppender.setLayout(patternLayout)
                appender name: 'mail', mailAppender

                break
            case grails.util.Environment.DEVELOPMENT:
                file name: 'bioAppender', file: '/logs/newwebbio.log', maxFileSize: 20000

                break
            case grails.util.Environment.TEST:
                break
            default: // caso non definito
                break
        } // fine del blocco switch
    }

    switch (grails.util.Environment.current) {
        case grails.util.Environment.PRODUCTION:
            root {
                // debug 'stdout'
                info 'bioAppender'
                // warn 'stdout'
                // error 'mail'
                additivity = true
            }
            break
        case grails.util.Environment.DEVELOPMENT:
            root {
                //debug 'stdout'
                info 'stdout', 'bioAppender'
                // warn 'stdout'
                //error 'mail'
                additivity = true
            }
            break
        case grails.util.Environment.TEST:
            root {
                //debug 'stdout'
                info 'stdout', 'bioAppender'
                // warn 'stdout'
                // error 'stdout','mail'
                additivity = false
            }
            break
        default: // caso non definito
            break
    } // fine del blocco switch

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'
}
*/
// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:

    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n')
        file name: 'bioAppender', file: '/logs/webbio.log', maxFileSize: 20000
        //file name: 'tomcatAppender', file: '/usr/local/apache-tomcat-6.0.18/logs/webbio.log', maxFileSize: 20000
        //file name: 'tomcatAppender2', file: '/usr/local/apache-tomcat-6.0.20/logs/webbio.log', maxFileSize: 20000
    }

    root {
        //debug 'stdout', 'bioAppender'
        info 'stdout', 'bioAppender'
        //warn 'stdout', 'bioAppender'
        //error 'stdout', 'bioAppender'
        additivity = true
    }

    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
            'org.springframework',
            'org.hibernate',
            'net.sf.ehcache.hibernate'

    warn 'org.mortbay.log'
}
