(deftemplate Task 
        (slot name)
        (slot task_type)
        (slot starting_time)
        (multislot required_resources)
        (slot duration)
)

(deftemplate Resource
        (slot resource_type)
        (slot name_resource)
)

(deftemplate Absolute_Timepoint
        (slot daytime_of_timepoint)
        (slot date_of_timepoint)
)

(deftemplate Date
        (slot year)
        (slot month)
        (slot day)
)

(deftemplate DayTime
        (slot hour)
        (slot second)
        (slot minute)
)

(deffunction addTask (?aTask) 
        "add a new task"
        (printout t "The new Task has ID " ?aTask)
        (return ?aTask)
)