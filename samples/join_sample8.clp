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
(defrule joinrule1
 (transaction
    (countryCode "US")
  )
  (account
    (cash ?c)
  )
  (rating
    (issuer "BOB")
  )
=>
  (printout t "joinrule1 was fired" crlf)
  (printout t "rule is done" )
  (bind ?sum (+ ?c 120.22) )
  (bind ?div (/ ?c 100.00) )
  (bind ?mul (* ?c 1.0) )
  (bind ?sub (- ?c 10.00) )
  (echo "hello " ?c)
  (batch "test.clp")
  (load "test.clp")
)
(ppdefrule "joinrule1")
