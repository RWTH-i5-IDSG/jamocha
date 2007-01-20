(deftemplate objectOne
  (slot intAttr (type INTEGER))
  (slot shortAttr (type SHORT))
  (slot longAttr (type LONG))
  (slot doubleAttr (type DOUBLE))
  (slot floatAttr (type FLOAT))
  (slot strAttr (type STRING))
)
(deftemplate objectTwo
  (slot intAttr (type INTEGER))
  (slot shortAttr (type SHORT))
  (slot longAttr (type LONG))
  (slot doubleAttr (type DOUBLE))
  (slot floatAttr (type FLOAT))
  (slot strAttr (type STRING))
)
(defrule joinConversion1
  (objectOne
    (intAttr ?intA)
  )
  (objectTwo
    (strAttr ?intA)
    (shortAttr 100)
  )
=>
  (printout t "joinConversion1 fired and int to string eval was good" crlf)
)
(defrule joinConversion2
  (objectOne
    (shortAttr ?shortA)
  )
  (objectTwo
    (strAttr ?shortA)
    (shortAttr 100)
  )
=>
  (printout t "joinConversion2 fired and int to string eval was good" crlf)
)
(defrule joinConversion3
  (objectOne
    (doubleAttr ?dbA)
  )
  (objectTwo
    (strAttr ?dbA)
    (shortAttr 100)
  )
=>
  (printout t "joinConversion3 fired and int to string eval was good" crlf)
)
(assert (objectOne (intAttr 100)(shortAttr 100)(longAttr 100)(doubleAttr 100.00)(floatAttr 100.00)(strAttr "100") ) )
(assert (objectTwo (intAttr 100)(shortAttr 100)(longAttr 100)(doubleAttr 100.00)(floatAttr 100.00)(strAttr "100") ) )
(fire)
