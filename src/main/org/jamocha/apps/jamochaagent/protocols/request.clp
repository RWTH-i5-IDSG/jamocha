(deffunction fipa-sl-request-handler "Handles incoming requests in FIPA-SL."
	(functiongroup "AgentFunctions")
	(?factid)
	
    (bind ?clipsCode (fact-slot-value ?factid content))
    ; if we have content in fipa-sl we parse it and translate it to CLIPS
    (if
    	(eq "fipa-sl" (str-lower (fact-slot-value ?factid language)))
     then
     	(bind ?clipsCode
	    	(sl2clips
	    		(fact-slot-value ?factid performative)
	    		?clipsCode
	    	)
	    )
    )
    ; here we evaluate the clips code in the engine
    ; and bind the result
    (if
    	(function-exists ?clipsCode)
     then
     	(agent-send-message 
	    	(fact-slot-value ?factid sender) 
	    	(fact-slot-value ?factid reply-to)
	    	"agree"
	    	(fact-slot-value ?factid content)
	    	(fact-slot-value ?factid language)
	    	(fact-slot-value ?factid encoding)
	    	(fact-slot-value ?factid ontology)
	    	(fact-slot-value ?factid protocol)
	    	(fact-slot-value ?factid conversation-id)
	    	(fact-slot-value ?factid reply-with)
	    	""
	    	NIL
	    )
	    (bind ?result 
	    	(eval ?clipsCode)
	    )
	    ; if the used language was fipa-sl we again need to retranslate our result
	    (if
	    	(eq "fipa-sl" (str-lower (fact-slot-value ?factid language)))
	     then
	     	(bind ?result (clips2sl ?result))
	    )
	    ; finally we send the result in an inform
	    (agent-send-message 
	    	(fact-slot-value ?factid sender) 
	    	(fact-slot-value ?factid reply-to)
	    	"inform"
	    	(str-cat (fact-slot-value ?factid content) crlf ?result) 
	    	(fact-slot-value ?factid language)
	    	(fact-slot-value ?factid encoding)
	    	(fact-slot-value ?factid ontology)
	    	(fact-slot-value ?factid protocol)
	    	(fact-slot-value ?factid conversation-id)
	    	(fact-slot-value ?factid reply-with)
	    	""
	    	NIL
	    )
	 else
	 	(agent-send-message 
	    	(fact-slot-value ?factid sender) 
	    	(fact-slot-value ?factid reply-to)
	    	"refuse"
	    	(fact-slot-value ?factid content)
	    	(fact-slot-value ?factid language)
	    	(fact-slot-value ?factid encoding)
	    	(fact-slot-value ?factid ontology)
	    	(fact-slot-value ?factid protocol)
	    	(fact-slot-value ?factid conversation-id)
	    	(fact-slot-value ?factid reply-with)
	    	""
	    	NIL
	    )
	)
)    

(assert 
    (agent-interaction-protocol 
        (protocol "fipa-request")
        (handlerfunction "fipa-sl-request-handler")
    )
)