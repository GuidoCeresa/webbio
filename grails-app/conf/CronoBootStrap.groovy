
class CronoBootStrap {

    // inject a reference to the Quartz Scheduler
    //org.quartz.Scheduler quartzScheduler

    def init = { servletContext ->

        //pass the quartzScheduler reference to some other classes
        //Engine.quartzScheduler = quartzScheduler
        //Engine.setTriggerExpression("0 0 1 ? * *") // una di notte tutti i giorni della settimana
       // Engine.start()
    }// fine della closure

    def destroy = {
    }// fine della closure

}// fine della classe
