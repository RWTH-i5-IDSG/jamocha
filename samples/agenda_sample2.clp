(deftemplate transaction
  (slot accountId (type STRING))
  (slot buyPrice (type DOUBLE))
  (slot countryCode (type STRING))
  (slot currentPrice (type DOUBLE))
  (slot cusip (type INTEGER))
  (slot exchange (type STRING))
  (slot industryGroupID (type INTEGER))
  (slot industryID (type INTEGER))
  (slot issuer (type STRING))
  (slot lastPrice (type DOUBLE))
  (slot purchaseDate (type STRING))
  (slot sectorID (type INTEGER))
  (slot shares (type DOUBLE))
  (slot subIndustryID (type INTEGER))
  (slot total (type DOUBLE))
)
(deftemplate account
  (slot accountId (type STRING))
  (slot cash (type DOUBLE))
  (slot fixedIncome (type DOUBLE))
  (slot stocks (type DOUBLE))
  (slot countryCode (type STRING))
)
(deftemplate rating
  (slot cusip (type INTEGER))
  (slot issuer (type STRING))
)
(defrule agenda1 (declare (salience 10)(rule-version a1.0) )
 (transaction
    (accountId ?accid)
    (countryCode "US")
  )
  (account
    (accountId ?accid)
  )
=>
  (printout t "agenda1 was fired" crlf)
)
(defrule agenda2 (declare (salience 100)(rule-version a1.0) )
 (transaction
    (countryCode "US")
  )
  (rating
    (issuer "MSFT")
  )
=>
  (printout t "agenda2 was fired" crlf)
)
(defrule agenda3 (declare (salience 100)(rule-version a1.0) )
 (transaction
    (countryCode "US")
  )
=>
  (printout t "agenda3 was fired" crlf)
)
(assert (transaction (accountId "acc1")(countryCode "US")(total 1298.00)(cusip 10101010)(issuer "BOB") ) )
(assert (account (accountId "acc1")(cash 1200000) ) )
(assert (rating (cusip 10101020)(issuer "MSFT") ) )
