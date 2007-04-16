
(deftemplate agent-description
    (slot name (type STRING))
    (slot local (type STRING))
)

(deftemplate agent-ontology
    (slot name (type STRING))
    (slot definition (type STRING))
)

(deftemplate agent-performative
    (slot performative (type STRING))
    (slot handlerfunction (type STRING))
)

(deftemplate agent-message
	(slot sender)
	(multislot receivers)
	(multislot reply-to)
	(slot performative)
	(slot content)
	(slot language)
	(slot encoding)
	(slot ontology)
	(slot protocol)
	(slot conversation-id)
	(slot in-reply-to)
	(slot reply-with)
	(slot reply-by)
	(multislot user-properties)
)


(defrule incoming-message 
	(agent-description
		(name ?receiver)
		(local TRUE)
	) 
	?message <- (agent-message
		(sender ?sender)
		(receivers ?receivers)
		(reply-to ?replyto)
		(performative ?performative)
		(content ?content)
		(language ?language)
		(encoding ?encoding)
		(ontology ?ontology)
		(protocol ?protocol)
		(conversation-id ?conversationid)
		(in-reply-to ?inreplyto)
		(reply-with ?replywith)
		(reply-by ?replyby)
		(user-properties ?userproperties)
	)
	(agent-performative 
	    (code ?performative)
	    (handlerfunction ?handler)
	)
	(test (member$ ?receiver ?receivers))
	=> 
	(printout t ?receiver " received a message with performative " ?performative crlf)	
	(assert (agent-description (name ?sender)(local FALSE)))
	(apply ?handler (member ?message getFactId))
)

(batch ontologies/fipasl.clp)
