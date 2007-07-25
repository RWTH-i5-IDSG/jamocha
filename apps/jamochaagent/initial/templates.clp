; ===================================================
; Definition of the templates that are needed.
; ===================================================

(deftemplate agent-identifier
	"A template defining an agent."
	; Name / address of an agent.
    (slot name (type STRING))
    
    ; Flag indicating if this agent is running locally or not.
    (slot local (type BOOLEAN)(default FALSE))
)


(deftemplate agent-message
	"Definition of an agent-message."
	(slot sender (type STRING)(default ""))
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
	(silent slot content-clips (type STRING))
	(silent slot timestamp (type LONG))
	(slot incoming (type BOOLEAN)(default TRUE))
	(slot processed (type BOOLEAN)(default FALSE))
)


(deftemplate agent-evaluation-error
	"Template for an error during evaluation."
	(slot message)
	(slot error (type STRING))
)


(deftemplate agent-acceptProposal-result
	"Result-Template for agree performatives."
	(slot message)
	(slot result)
)


(deftemplate agent-agree-result
	"Result-Template for agree performatives."
	(slot message)
	(slot action (type STRING))
	(slot proposition (type STRING))
)


(deftemplate agent-cancel-result
	"Result template for cancel performatives."
	(slot message)
	(slot initiator (type STRING))
	(slot performative (type STRING))
	(slot messageContent (type STRING))
)


(deftemplate agent-cfp-result
	"Result-Template for cfp performatives."
	(slot message)
	(slot action (type STRING))
	(slot refOp (type STRING))
	(multislot items)
)


(deftemplate agent-confirm-result
	"Result-Template for confirm performatives."
	(slot message)
	(multislot propositions)
)


(deftemplate agent-disconfirm-result
	"Result-Template for disconfirm performatives."
	(slot message)
	(multislot propositions)
)


(deftemplate agent-failure-result
	"Result-Template for failure performatives."
	(slot message)
	(slot action (type STRING))
	(slot proposition (type STRING))
)


(deftemplate agent-propose-result
	"Result-Template for propose performatives."
	(slot message)
	(slot action (type STRING))
	(multislot propositions)
)


(deftemplate agent-queryIf-result
	"Result-Template for query-if performatives."
	(slot message)
	(slot result (type BOOLEAN)(default FALSE))
)


(deftemplate agent-queryRef-result
	"Result-Template for query-ref performatives."
	(slot message)
	(slot refOp (type STRING))
	(multislot items)
)


(deftemplate agent-request-result
	"Result-Template for request performatives."
	(slot message)
	(slot result)
)


(deftemplate agent-requestWhen-result
	"Result template for request-when performatives."
	(slot message)
	(slot result)
)

(deftemplate agent-message-rule-pairing
	"Generic pairing to connect a rule to the message that caused its definition."
	(slot message)
	(slot ruleName (type STRING))
)




(deftemplate inform
	"A generic template for inform messages. It needs to be connected to some other construct (e.g. agent-requestWhen-result) to fire a certain rule."
	(slot sender)
	(multislot receiver)
	(slot content (type STRING))
	; for now these three fields are enough.
	; we might want to tread like any other agent-message.
)

(deftemplate inform-if
	"A generic template for inform messages. It needs to be connected to some other construct (e.g. agent-requestWhen-result) to fire a certain rule."
	(slot sender)
	(multislot receiver)
	(slot content (type STRING))
	; for now these three fields are enough.
	; we might want to tread like any other agent-message.
)

(deftemplate inform-ref
	"A generic template for inform messages. It needs to be connected to some other construct (e.g. agent-requestWhen-result) to fire a certain rule."
	(slot sender)
	(multislot receiver)
	(slot content (type STRING))
	; for now these three fields are enough.
	; we might want to tread like any other agent-message.
)