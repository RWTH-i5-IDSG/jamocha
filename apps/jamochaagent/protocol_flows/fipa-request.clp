(bind ?fipa-request-0 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 0) 
		(incoming-speechact NIL) 
		(agent-type NIL) 
		(previous-state NIL)
	)
))

(bind ?fipa-request-1 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 1) 
		(incoming-speechact "request") 
		(agent-type "initiator") 
		(previous-state ?fipa-request-0)
	)
))

(bind ?fipa-request-2 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 2) 
		(incoming-speechact "refuse") 
		(agent-type "participant") 
		(previous-state ?fipa-request-1)
	)
))

(bind ?fipa-request-3 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 3) 
		(incoming-speechact "agree") 
		(agent-type "participant") 
		(previous-state ?fipa-request-1)
	)
))

(bind ?fipa-request-4 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 4) 
		(incoming-speechact "failure") 
		(agent-type "participant") 
		(previous-state ?fipa-request-3)
	)
))

(bind ?fipa-request-5 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 5) 
		(incoming-speechact "inform-done") 
		(agent-type "participant") 
		(previous-state ?fipa-request-3)
	)
))

(bind ?fipa-request-6 (assert
	(ip-state
		(protocol-name "fipa-request") 
		(state-name 6) 
		(incoming-speechact "inform-result") 
		(agent-type "participant") 
		(previous-state ?fipa-request-3)
	)
))