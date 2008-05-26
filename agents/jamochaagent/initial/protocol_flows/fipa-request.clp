(bind ?fipa-request-0 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 0)
	)
))

; got request
(bind ?fipa-request-1 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 1)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-request-0)
		(to-state ?fipa-request-1)
		(speechact "request")
		(agent-type "initiator")
	)
)

; refused
(bind ?fipa-request-2 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 2)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-request-1)
		(to-state ?fipa-request-2)
		(speechact "refuse")
		(agent-type "participant")
	)
)

; agreed
(bind ?fipa-request-3 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 3)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-request-1)
		(to-state ?fipa-request-3)
		(speechact "agree")
		(agent-type "participant")
	)
)

; failed
(bind ?fipa-request-4 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 4)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-request-3)
		(to-state ?fipa-request-4)
		(speechact "failure")
		(agent-type "participant")
	)
)

; informed
(bind ?fipa-request-5 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 5)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-request-3)
		(to-state ?fipa-request-5)
		(speechact "inform")
		(agent-type "participant")
	)
)