(defrule PingAgent
	"This is the PingAgent in just three rules! This is when everything is right."
	(agent-identifier
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(performative "query-ref")
		(receiver ?receivers)
		(content "ping")
		; (language "pinging") ; needed?
		(incoming TRUE)
		(processed FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver ?receivers) 0))

	=>

	(bind ?newReceivers (prepare-receivers ?message))
	
	(process-outgoing-message 
		(assert 
			(agent-message
				(receiver ?newReceivers)
				(performative "inform")
				(content "alive")
				(language (fact-slot-value ?message "language"))
				(encoding (fact-slot-value ?message "encoding"))
				(ontology (fact-slot-value ?message "ontology"))
				(protocol (fact-slot-value ?message "protocol"))
				(conversation-id (fact-slot-value ?message "conversation-id"))
				(in-reply-to (fact-slot-value ?message "reply-with"))
				(reply-with "")
				(reply-by 0)
				(timestamp (datetime2timestamp (now)))
				(incoming FALSE)
			)
		)
	)
	; Set the message to processed.
	(modify ?message (processed TRUE))
)


(defrule PingAgent-wrongPerformative
	"This is the PingAgent in just three rules! This checks for wrong performative."
	(agent-identifier
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(performative ?performative&~"not-understood"&~"query-ref")
		(receiver ?receivers)
		; (language "pinging") ; needed?
		(incoming TRUE)
		(processed FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver ?receivers) 0))

	=>

	(bind ?newReceivers (prepare-receivers ?message))
	
	(bind ?newContent (str-cat
		"( (Unexpected-act " ?performative ") ( expected (query-ref :content ping)))"
	))

	(process-outgoing-message 
		(assert 
			(agent-message
				(receiver ?newReceivers)
				(performative "not-understood")
				(content ?newContent)
				(language (fact-slot-value ?message "language"))
				(encoding (fact-slot-value ?message "encoding"))
				(ontology (fact-slot-value ?message "ontology"))
				(protocol (fact-slot-value ?message "protocol"))
				(conversation-id (fact-slot-value ?message "conversation-id"))
				(in-reply-to (fact-slot-value ?message "reply-with"))
				(reply-with "")
				(reply-by 0)
				(timestamp (datetime2timestamp (now)))
				(incoming FALSE)
			)
		)
	)
	; Set the message to processed.
	(modify ?message (processed TRUE))
)



(defrule PingAgent-wrongContent
	"This is the PingAgent in just three rules! This checks for wrong message content."
	(agent-identifier
		(name ?receiver)
		(local TRUE)
	)
	; Only extract messages that have not been answered yet and that are incoming.
	?message <- (agent-message
		(performative "query-ref")
		(receiver ?receivers)
		(content ?content&~"ping")
		; (language "pinging") ; needed?
		(incoming TRUE)
		(processed FALSE)
	)
	; The receiver of the message must be local.
	(test (> (member$ ?receiver ?receivers) 0))

	=>

	(bind ?newReceivers (prepare-receivers ?message))
	
	(bind ?newContent "(UnexpectedContent (expected ping))")

	(process-outgoing-message 
		(assert 
			(agent-message
				(receiver ?newReceivers)
				(performative "not-understood")
				(content ?newContent)
				(language (fact-slot-value ?message "language"))
				(encoding (fact-slot-value ?message "encoding"))
				(ontology (fact-slot-value ?message "ontology"))
				(protocol (fact-slot-value ?message "protocol"))
				(conversation-id (fact-slot-value ?message "conversation-id"))
				(in-reply-to (fact-slot-value ?message "reply-with"))
				(reply-with "")
				(reply-by 0)
				(timestamp (datetime2timestamp (now)))
				(incoming FALSE)
			)
		)
	)
	; Set the message to processed.
	(modify ?message (processed TRUE))
)