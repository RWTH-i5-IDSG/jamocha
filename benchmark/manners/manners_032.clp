; load initial data
(printout t "loading the initial data")
(batch benchmark/manners/init.clp)

; load the guests
(printout t "loading the guests")
(load-facts benchmark/manners/032_guests.dat)

; start the manners benchmark
(printout t "starting manners benchmark")
(batch benchmark/manners/start.clp)