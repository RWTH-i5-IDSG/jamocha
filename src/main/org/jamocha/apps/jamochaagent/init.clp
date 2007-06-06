
(deftemplate agent-description
    (slot name (type STRING))
    (slot local (type BOOLEAN))
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
	(slot sender (type STRING))
	(multislot receivers)
	(multislot reply-to)
	(slot performative (type STRING))
	(slot content (type STRING))
	(slot language (type STRING))
	(slot encoding (type STRING))
	(slot ontology (type STRING))
	(slot protocol (type STRING))
	(slot conversation-id (type STRING))
	(slot in-reply-to (type STRING))
	(slot reply-with (type STRING))
	(slot reply-by (type LONG))
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
	    (performative ?performative)
	    (handlerfunction ?handler)
	)
	(test (> (member$ ?receiver ?receivers) 0))
	=> 
	(printout t ?receiver " received a message with performative " ?performative)	
	(assert (agent-description (name ?sender)(local FALSE)))
	(apply ?handler (get-fact-id ?message))
)

; (batch apps/jamochaagent/performatives/fipasl.clp)