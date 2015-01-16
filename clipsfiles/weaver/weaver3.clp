

(defrule p490
  ?c <- (context (present separate))
  =>
  (modify ?c (present reconnect))
)

(defrule p491
  ?c <- (context (present reconnect))
  =>
  (modify ?c (present form-verti))
)

(defrule p492
  ?c <- (context (present form-verti))
  =>
  (modify ?c (present partial-route))
)

(defrule p493
  ?c <- (context (present partial-route))
  =>
  (modify ?c (present extend-pins))
)

(defrule p494
  ?c1 <-  (context (present extend-pins))
  =>
  (modify ?c1 (present nil) (previous extend-pins))
)

(defrule p495
  ?c1 <-  (context (previous extend-pins))
  =>
  (assert (switch-box))
  (assert (context (present propagate-constraint)))
  (retract ?c1)
)

(defrule p496
  ?c1 <-  (context (previous extend-pins))
  (channel (no-of-left-pins 0) (no-of-right-pins 0))
  =>
  (retract ?c1)
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p497
  ?c1 <-  (context (previous extend-pins))
  (channel (no-of-bottom-pins 0) (no-of-top-pins 0))
  =>
  (retract ?c1)
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p498
  ?f1 <- (context (previous propagate-constraint))
  (net (net-is-routed ~yes))
  =>
  (retract ?f1)
  (assert (context (present lshape1)))
)

(defrule p499
  ?f1 <- (context (present lshape1))
  =>
  (modify ?f1 (present find-no-of-pins-on-a-row-col))
)

(defrule p500
  ?c <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  (not (extend-ff-tried))
  =>
  (retract ?c)
  (assert (context (present lshape4)))
)

(defrule p501
  ?c <- (context (present lshape4))
  =>
  (retract ?c)
  (assert (extend-ff-tried))
  (assert (context (present extend-ff)))
)

(defrule p502
  ?c <- (context (present extend-ff))
  =>
  (retract ?c)
  (assert (context (previous extend-ff)))
)

(defrule p503
  ?c <- (context (previous extend-ff))
  (not (extended-ff ? ?))
  =>
  (retract ?c)
  (assert (context (present find-no-of-pins-on-a-row-col)))
)

(defrule p504
  ?c <- (context (previous extend-ff))
  ?e <- (extended-ff ? ?)
  ?ex <- (extend-ff-tried)
  =>
  (retract ?c ?e ?ex)
  (assert (context (present find-no-of-pins-on-a-row-col)))
  (assert (goal cleanup extended-ff))
)

(defrule p505
  ?f1 <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  (extend-ff-tried)
  (not (total-verti ? ? ? ?))
  =>
  (retract ?f1)
  (assert (context (present find-no-of-vcg-hcg)))
  (assert (goal cleanup counted-verti))
)

(defrule p506
  ?f1 <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  (total-verti ? ? ? ?)
  (not (verti-has-loop))
  =>
  (retract ?f1)
  (assert (verti-has-loop))
  (assert (context (present choose-between-total-verti)))
)

(defrule p507
  ?f1 <- (context (previous find-no-of-pins-on-a-row-col))
  (not (total))
  ?f3 <- (total-verti ? ? ? ?)
  (verti-has-loop)
  =>
  (assert (context (present extend-h-v-s)))
  (assert (goal cleanup total-verti))
  (assert (goal cleanup counted-verti))
  (assert (goal cleanup extend-ff-tried))
  (retract ?f1 ?f3)
)

(defrule p508
  ?f1 <- (context (present choose-between-total-verti))
  (not (total-verti ? ? ?qw50&:(> ?qw50 0) ?))
  =>
  (modify ?f1 (present extend-h-v-s))
  (assert (goal cleanup counted-verti))
)

(defrule p509
  ?f1 <- (context (previous find-no-of-vcg-hcg))
  (not (total-verti ? ? ? ?))
  =>
  (retract ?f1)
  (assert (context (present extend-h-v-s)))
)

(defrule p510
  ?f1 <- (context (present loose-constraint))
  =>
  (assert (context (present lshape2)))
  (retract ?f1)
)

(defrule p511
  ?f1 <- (context (present lshape2))
  =>
  (modify ?f1 (present lshape3))
)

(defrule p512
  ?f1 <- (context (present lshape3))
  =>
  (retract ?f1)
  (assert (context (present random0)))
)

(defrule p513
  ?f1 <- (context (present random0))
  =>
  (retract ?f1)
  (assert (context (present random1)))
)

(defrule p514
  (context (present remove-routed-net-segments))
  (not (net (net-is-routed ~yes)))
  (not (horizontal (net-name ~nil) (status nil)))
  (not (vertical (net-name ~nil) (status nil)))
  =>
  (printout t "Finished with the routing." crlf)
  (halt)
)


(defrule p1
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (com ?min))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) (pin-channel-side top))
  (not (horizontal-s (net-name ?nn) (min ?qw43&:(<= ?qw43 ?px)) 
       (max ?qw51&:(>= ?qw51 ?px)) (id ~?id)))
  (pin (net-name ~?nn) (pin-x ?min) (pin-channel-side top))
  (not (pin (net-name ?nn) (pin-x ?px) (pin-channel-side bottom)))
  (not (pin (net-name ?nn) (pin-x ?qw52&:(and (< ?qw52 ?px) (> ?qw52 ?min)))))
  (pin (net-name ?nn) (pin-x ?qw53&:(> ?qw53 ?px)) (pin-channel-side top))
  =>
  (modify ?h (min ?px))
  (assert (horizontal-s (net-name ?nn) (min ?min) (max ?px) (com ?com) 
                        (id =(+ ?id 1000)) (top-count 1) (bot-count 1) 
                        (difference 0) (absolute 0) (sum 2)))
)

(defrule p2
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (com ?min))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) (pin-channel-side bottom))
  (not (horizontal-s (net-name ?nn) (min ?qw43&:(<= ?qw43 ?px)) 
                     (max ?qw51&:(>= ?qw51 ?px))(id ~?id)))
  (pin (net-name ~?nn) (pin-x ?min) (pin-channel-side bottom))
  (not (pin (net-name ?nn) (pin-x ?px) (pin-channel-side top)))
  (not (pin (net-name ?nn) (pin-x ?qw52&:(and (< ?qw52 ?px) (> ?qw52 ?min)))))
  (pin (net-name ?nn) (pin-x ?qw53&:(> ?qw53 ?px)) (pin-channel-side bottom))
  =>
  (modify ?h (min ?px))
  (assert (horizontal-s (net-name ?nn) (min ?min) (max ?px) (com ?com)
                        (id =(+ ?id 1000)) (top-count 1) (bot-count 1) 
                        (difference 0) (absolute 0) (sum 2)))
)

(defrule p3
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (com ?max))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) (pin-channel-side top))
  (not (horizontal-s (net-name ?nn) (min ?qw43&:(<= ?qw43 ?px)) 
                     (max ?qw51&:(>= ?qw51 ?px)) (id ~?id)))
  (pin (net-name ~?nn) (pin-x ?max) (pin-channel-side top))
  (not (pin (net-name ?nn) (pin-x ?px) (pin-channel-side bottom)))
  (not (pin (net-name ?nn) (pin-x ?qw54&:(and (< ?qw54 ?max) (> ?qw54 ?px)))))
  (pin (net-name ?nn) (pin-x ?qw55&:(< ?qw55 ?px)) (pin-channel-side top))
  =>
  (modify ?h (max ?px))
  (assert (horizontal-s (net-name ?nn) (min ?px) (max ?max) (com ?com)
                        (id =(+ ?id 1000)) (top-count 1) (bot-count 1) 
                        (difference 0) (absolute 0) (sum 2)))
)

(defrule p4
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (com ?max))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) (pin-channel-side bottom))
  (not (horizontal-s (net-name ?nn) (min ?qw43&:(<= ?qw43 ?px)) 
                     (max ?qw51&:(>= ?qw51 ?px)) (id ~?id)))
  (pin (net-name ~?nn) (pin-x ?max) (pin-channel-side bottom))
  (not (pin (net-name ?nn) (pin-x ?px) (pin-channel-side top)))
  (not (pin (net-name ?nn) (pin-x ?qw54&:(and (< ?qw54 ?max) (> ?qw54 ?px)))))
  (pin (net-name ?nn) (pin-x ?qw55&:(< ?qw55 ?px)) (pin-channel-side bottom))
  =>
  (modify ?h (max ?px))
  (assert (horizontal-s (net-name ?nn) (min ?px) (max ?max) (com ?com) 
                        (id =(+ ?id 1000)) (top-count 1) (bot-count 1) 
                        (difference 0) (absolute 0) (sum 2)))
)

(defrule p5
  (context (present reconnect))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?min1) (max ?min) (id ?id2))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side top))
  (not (pin (pin-x ?max) (pin-channel-side bottom)))
  (not (pin (net-name ?nn) (pin-x ?qw55&:(and (< ?qw55 ?max) (> ?qw55 ?min)))))
  (last-row ?lr)
  (not (horizontal-s (net-name ?nn) (id ?id1) (com ?qw56&:(and (> ?qw56 1) (< ?qw56 ?lr)))))
  (not (horizontal-s (net-name ?nn) (id ?id2) (com ?qw57&:(and (> ?qw57 1) (< ?qw57 ?lr)))))
  =>
  (retract ?h1)
  (assert (vertical-s (net-name ?nn) (min 1) (max ?lr) (com ?max) 
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
  (modify ?h2 (max ?max))
)

(defrule p6
  (context (present reconnect))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?max1) (id ?id2))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side bottom))
  (not (pin (pin-x ?min) (pin-channel-side bottom)))
  (not (pin (net-name ?nn) (pin-x ?qw55&:(and (< ?qw55 ?max) (> ?qw55 ?min)))))
  (last-row ?lr)
  (not (horizontal-s (net-name ?nn) (id ?id1) (com ?qw56&:(and (> ?qw56 1) (< ?qw56 ?lr)))))
  (not (horizontal-s (net-name ?nn) (id ?id2) (com ?qw57&:(and (> ?qw57 1) (< ?qw57 ?lr)))))
  =>
  (retract ?h1)
  (assert (vertical-s (net-name ?nn) (min 1) (max ?lr) (com ?min) 
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
  (modify ?h2 (min ?min))
)

(defrule p7
  (context (present reconnect))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?min1) (max ?min) (id ?id2))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side top))
  (not (pin (pin-x ?max) (pin-channel-side top)))
  (not (pin (net-name ?nn) (pin-x ?qw55&:(and (< ?qw55 ?max) (> ?qw55 ?min)))))
  (last-row ?lr)
  (not (horizontal-s (net-name ?nn) (id ?id1) (com ?qw56&:(and (> ?qw56 1) (< ?qw56 ?lr)))))
  (not (horizontal-s (net-name ?nn) (id ?id2) (com ?qw57&:(and (> ?qw57 1) (< ?qw57 ?lr)))))
  =>
  (retract ?h1)
  (assert (vertical-s (net-name ?nn) (min 1) (max ?lr) (com ?max)
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
  (modify ?h2 (max ?max))
)



(defrule p8
  (context (present reconnect))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?max1) (id ?id2))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side bottom))
  (not (pin (pin-x ?min) (pin-channel-side top)))
  (not (pin (net-name ?nn) (pin-x ?qw55&:(and (< ?qw55 ?max) (> ?qw55 ?min)))))
  (last-row ?lr)
  (not (horizontal-s (net-name ?nn) (id ?id1) (com ?qw56&:(and (> ?qw56 1) (< ?qw56 ?lr)))))
  (not (horizontal-s (net-name ?nn) (id ?id2) (com ?qw57&:(and (> ?qw57 1) (< ?qw57 ?lr)))))
  =>
  (retract ?h1)
  (assert (vertical-s (net-name ?nn) (min 1) (max ?lr) (com ?min)
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
  (modify ?h2 (min ?min))
)

(defrule p9
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (horizontal-s (net-name ?nn) (min ?min1&:(>= ?min1 ?min)) 
                (max ?max1&:(<= ?max1 ?max)) (com ~?com))
  ?v <- (vertical-s (net-name ?nn) (com ?com1&:(and (>= ?com1 ?min1) (<= ?com1 ?max1))))
  (pin (net-name ~?nn) (pin-x ?com1))
  (congestion (direction col) (coordinate ?px&:(and (>= ?px ?min1) (<= ?px ?max1))))
  (not (pin (net-name ~?nn) (pin-x ?px)))
  (not (vertical-s (com ?px)))
  =>
  (modify ?v (com ?px))
)

(defrule p10
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com))
  (horizontal-s (net-name ?nn) (min ?min1&:(and (>= ?min1 ?min) (<= ?min1 ?max))) 
                (max ?max1&:(>= ?max1 ?max)) (com ~?com))
  ?v <- (vertical-s (net-name ?nn) (com ?com1&:(and (>= ?com1 ?min1) (<= ?com1 ?max))))
  (pin (net-name ~?nn) (pin-x ?com1))
  (congestion (direction col) (coordinate ?px&:(and (>= ?px ?min1) (<= ?px ?max))))
  (not (pin (net-name ~?nn) (pin-x ?px)))
  (not (vertical-s (com ?px)))
  =>
  (modify ?v (com ?px))
)

(defrule p11
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom) (id ?id))
  (horizontal-s (net-name ?nn) (min ?min1&:(>= ?min1 ?min))
                (max ?max1&:(<= ?max1 ?max)) (com ~?hcom))
  (not (vertical-s (net-name ?nn) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max1)))))
  ?v <- (vertical-s (net-name ?nn) (com ?vcom&:(< ?vcom ?min1)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                     (max ?qw62&:(>= ?qw62 ?vcom)) (id ~?id)))
  =>
  (modify ?v (com ?min1))
)

(defrule p12
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom) (id ?id))
  (horizontal-s (net-name ?nn) (min ?min1&:(>= ?min1 ?min)) 
                (max ?max1&:(<= ?max1 ?max)) (com ~?hcom))
  (not (vertical-s (net-name ?nn) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max1)))))
  ?v <- (vertical-s (net-name ?nn) (com ?vcom&:(> ?vcom ?max1)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                     (max ?qw62&:(>= ?qw62 ?vcom)) (id ~?id)))
  =>
  (modify ?v (com ?max1))
)

(defrule p13
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom) (id ?id))
  (horizontal-s (net-name ?nn) (min ?min1&:(and (>= ?min1 ?min) (<= ?min1 ?max))) 
                (max ?max1&:(>= ?max1 ?max)) (com ~?hcom))
  (not (vertical-s (net-name ?nn) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max)))))
  ?v <- (vertical-s (net-name ?nn) (com ?vcom&:(< ?vcom ?min1)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                     (max ?qw62&:(>= ?qw62 ?vcom)) (id ~?id)))
  =>
  (modify ?v (com ?min1))
)

(defrule p14
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom) (id ?id))
  (horizontal-s (net-name ?nn) (min ?min1&:(and (>= ?min1 ?min) (<= ?min1 ?max))) 
                (max ?max1&:(>= ?max1 ?max)) (com ~?hcom))
  (not (vertical-s (net-name ?nn) (com ?qw60&:(and (>= ?qw60 ?min1) (<= ?qw60 ?max)))))
  ?v <- (vertical-s (net-name ?nn) (com ?vcom&:(> ?vcom ?max)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                     (max ?qw62&:(>= ?qw62 ?vcom)) (id ~?id)))
  =>
  (modify ?v (com ?max))
)

(defrule p15
  (context (present reconnect))
  (horizontal-s (net-name ?nn) (min ?min1) (max ?max1) (id ?id1))
  ?h <- (horizontal-s (net-name ?nn) (min ?max1) (max ?max2) (com ?com) (id ?id2))
  (horizontal-s (net-name ?nn) (min ?max2) (max ?max3) (id ?id3))
  (not (horizontal-s (net-name ?nn) (id ~?id1&~?id2&~?id3)))
  (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
              (max ?qw62&:(>= ?qw62 ?com)) (com ?max1))
  (vertical-s (net-name ?nn) (min ?qw75&:(<= ?qw75 ?com)) 
              (max ?qw63&:(>= ?qw63 ?com)) (com ?max2))
  =>
  (modify ?h (min ?min1) (max ?max3))
)

(defrule p16
  (context (present reconnect))
  (vertical-s (net-name ?nn) (min ?min1) (max ?max1) (id ?id1))
  ?h <- (vertical-s (net-name ?nn) (min ?max1) (max ?max2) (com ?com) (id ?id2))
  (vertical-s (net-name ?nn) (min ?max2) (max ?max3) (id ?id3))
  (not (vertical-s (net-name ?nn) (id ~?id1&~?id2&~?id3)))
  (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                (max ?qw62&:(>= ?qw62 ?com)) (com ?max1))
  (horizontal-s (net-name ?nn) (min ?qw75&:(<= ?qw75 ?com)) 
                (max ?qw63&:(>= ?qw63 ?com)) (com ?max2))
  =>
  (modify ?h (min ?min1) (max ?max3))
)

(defrule p17
  (context (present separate))
  ?v <- (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?com) (com ?max))
  (pin (net-name ?nn) (pin-y ?py&:(and (> ?py ?min) (< ?py ?max))) 
       (pin-channel-side right))
  (not (pin (net-name ?nn) (pin-x ?qw86&:(> ?qw86 ?com)) 
            (pin-y ?qz30&:(and (> ?qz30 ?py) (<= ?qz30 ?max)))))
  (not (pin (net-name ?nn) (pin-x ?com) (pin-channel-side top)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                     (max ?qw62&:(>= ?qw62 ?com)) 
                     (com ?qz38&:(and (> ?qz38 ?py) (< ?qz38 ?max)))))
  (not (horizontal-s (net-name ?nn) (min ?com) (max ?hmax) (com ?max)))
  =>
  (modify ?v (max ?py))
  (assert (vertical-s (net-name ?nn) (min ?py) (max ?max) (com ?com) (id =(+ ?id 1000)) 
                      (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p18
  (context (present separate))
  ?v <- (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (horizontal-s (net-name ?nn) (min ?com) (max ?hmax) (com ?max))
  (pin (net-name ?nn) (pin-y ?py&:(and (> ?py ?min) (< ?py ?max))) 
       (pin-channel-side left))
  (not (pin (net-name ?nn) (pin-x ?qz20&:(< ?qz20 ?com)) 
            (pin-y ?qz30&:(and (> ?qz30 ?py) (<= ?qz30 ?max)))))
  (not (pin (net-name ?nn) (pin-x ?com) (pin-channel-side top)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                     (max ?qw62&:(>= ?qw62 ?com)) 
                     (com ?qz38&:(and (> ?qz38 ?py) (< ?qz38 ?max)))))
  (not (horizontal-s (net-name ?nn) (min ?hmin) (max ?com) (com ?max)))
  =>
  (modify ?v (max ?py))
  (assert (vertical-s (net-name ?nn) (min ?py) (max ?max) (com ?com) (id =(+ ?id 1000)) 
                      (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p19
  (context (present separate))
  ?v <- (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?com) (com ?min))
  (pin (net-name ?nn) (pin-y ?py&:(and (> ?py ?min) (< ?py ?max))) 
       (pin-channel-side right))
  (not (pin (net-name ?nn) (pin-x ?qw86&:(> ?qw86 ?com)) 
            (pin-y ?qz47&:(and (< ?qz47 ?py) (>= ?qz47 ?min)))))
  (not (pin (net-name ?nn) (pin-x ?com) (pin-channel-side bottom)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                     (max ?qw62&:(>= ?qw62 ?com))
                     (com ?qz38&:(and (> ?qz38 ?min) (< ?qz38 ?py)))))
  (not (horizontal-s (net-name ?nn) (min ?com) (max ?hmax) (com ?min)))
  =>
  (modify ?v (min ?py))
  (assert (vertical-s (net-name ?nn) (min ?min) (max ?py) (com ?com) (id =(+ ?id 1000)) 
                      (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p20
  (context (present separate))
  ?v <- (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (horizontal-s (net-name ?nn) (min ?com) (max ?hmax) (com ?min))
  (pin (net-name ?nn) (pin-y ?py&:(and (> ?py ?min) (< ?py ?max))) 
       (pin-channel-side left))
  (not (pin (net-name ?nn) (pin-x ?qz20&:(< ?qz20 ?com)) 
            (pin-y ?qz47&:(and (< ?qz47 ?py) (>= ?qz47 ?min)))))
  (not (pin (net-name ?nn) (pin-x ?com) (pin-channel-side bottom)))
  (not (horizontal-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                     (max ?qw62&:(>= ?qw62 ?com)) 
                     (com ?qz38&:(and (> ?qz38 ?min) (< ?qz38 ?py)))))
  (not (horizontal-s (net-name ?nn) (min ?hmin) (max ?com) (com ?min)))
  =>
  (modify ?v (min ?py))
  (assert (vertical-s (net-name ?nn) (min ?min) (max ?py) (com ?com) (id =(+ ?id 1000)) 
                      (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p21
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (min ?vmin) (max ?com) (com ?min))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) 
       (pin-channel-side top))
  (not (pin (net-name ?nn) (pin-x ?qz47&:(and (< ?qz47 ?px) (>= ?qz47 ?min))) 
            (pin-y ?qw86&:(> ?qw86 ?com))))
  (not (pin (net-name ?nn) (pin-y ?com) (pin-channel-side left)))
  (not (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                   (max ?qw62&:(>= ?qw62 ?com)) 
                   (com ?qz38&:(and (> ?qz38 ?min) (< ?qz38 ?px)))))
  (not (vertical-s (net-name ?nn) (min ?com) (max ?vmax) (com ?min)))
  =>
  (modify ?h (min ?px))
  (assert (horizontal-s (net-name ?nn) (min ?min) (max ?px) (com ?com) (id =(+ ?id 1000)) 
                        (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p22
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (min ?com) (max ?vmax) (com ?min))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) 
       (pin-channel-side bottom))
  (not (pin (net-name ?nn) (pin-x ?qz47&:(and (< ?qz47 ?px) (>= ?qz47 ?min))) 
            (pin-y ?qz20&:(< ?qz20 ?com))))
  (not (pin (net-name ?nn) (pin-y ?com) (pin-channel-side left)))
  (not (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                   (max ?qw62&:(>= ?qw62 ?com)) 
                   (com ?qz38&:(and (> ?qz38 ?min) (< ?qz38 ?px)))))
  (not (vertical-s (net-name ?nn) (min ?vmin) (max ?com) (com ?min)))
  =>
  (modify ?h (min ?px))
  (assert (horizontal-s (net-name ?nn) (min ?min) (max ?px) (com ?com) (id =(+ ?id 1000)) 
                        (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p23
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (min ?vmin) (max ?com) (com ?max))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max)))
       (pin-channel-side top))
  (not (pin (net-name ?nn) (pin-x ?qz30&:(and (> ?qz30 ?px) (<= ?qz30 ?max))) 
            (pin-y ?qw86&:(> ?qw86 ?com))))
  (not (pin (net-name ?nn) (pin-y ?com) (pin-channel-side right)))
  (not (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                   (max ?qw62&:(>= ?qw62 ?com)) 
                   (com ?qz38&:(and (> ?qz38 ?px) (< ?qz38 ?max)))))
  (not (vertical-s (net-name ?nn) (min ?com) (max ?vmax) (com ?max)))
  =>
  (modify ?h (max ?px))
  (assert (horizontal-s (net-name ?nn) (min ?px) (max ?max) (com ?com) (id =(+ ?id 1000)) 
                        (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)

(defrule p24
  (context (present separate))
  ?h <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?com) (id ?id))
  (vertical-s (net-name ?nn) (min ?com) (max ?vmax) (com ?max))
  (pin (net-name ?nn) (pin-x ?px&:(and (> ?px ?min) (< ?px ?max))) 
       (pin-channel-side bottom))
  (not (pin (net-name ?nn) (pin-x ?qz30&:(and (> ?qz30 ?px) (<= ?qz30 ?max))) 
            (pin-y ?qz20&:(< ?qz20 ?com))))
  (not (pin (net-name ?nn) (pin-y ?com) (pin-channel-side right)))
  (not (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?com)) 
                   (max ?qw62&:(>= ?qw62 ?com)) 
                   (com ?qz38&:(and (> ?qz38 ?px) (< ?qz38 ?max)))))
  (not (vertical-s (net-name ?nn) (min ?vmin) (max ?com) (com ?max)))
  =>
  (modify ?h (max ?px))
  (assert (horizontal-s (net-name ?nn) (min ?px) (max ?max) (com ?com) (id =(+ ?id 1000)) 
                        (top-count 1) (bot-count 1) (difference 0) (absolute 0) (sum 2)))
)



(defrule p604
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))) 
       (pin-channel-side top))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1))
  (not (horizontal-s (net-name ?bnn) (min ?qw75&:(<= ?qw75 ?vcom))
                     (max ?qw63&:(>= ?qw63 ?vcom)) (id ~?id1)))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side bottom))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) 
                   (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) 
                      (pin-name-2 ?bpn) 
                      (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type vertical)))
)

(defrule p605
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))) 
       (pin-channel-side bottom))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1))
  (not (horizontal-s (net-name ?bnn) (min ?qw75&:(<= ?qw75 ?vcom)) 
                     (max ?qw63&:(>= ?qw63 ?vcom)) (id ~?id1)))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side top))
  (not (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) 
                   (pin-name-2 ?tpn)))
  =>
  (assert (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) 
                      (pin-name-2 ?tpn)
                      (seg-id-1 ?id1) (seg-id-2 ?id) (constraint-type vertical)))
)



(defrule p606
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))) 
       (pin-channel-side top))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1) (com 1))
  (horizontal-s (net-name ?bnn) (min ?qw75&:(<= ?qw75 ?vcom)) 
                (max ?qw63&:(>= ?qw63 ?vcom)) (id ~?id1))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side bottom))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) 
                   (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) 
                      (pin-name-2 ?bpn) (seg-id-1 ?id) (seg-id-2 ?id1) 
                      (constraint-type vertical)))
)

(defrule p607
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))) 
       (pin-channel-side bottom))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) 
                (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1) (com ~1))
  (horizontal-s (net-name ?bnn) (min ?qw75&:(<= ?qw75 ?vcom)) 
                (max ?qw63&:(>= ?qw63 ?vcom)) (id ~?id1))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side top))
  (not (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) 
                   (pin-name-2 ?tpn)))
  =>
  (assert (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn) 
                      (seg-id-1 ?id1) (seg-id-2 ?id) (constraint-type vertical)))
)

(defrule p608
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference ~0))
  (vertical-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?com) (id ?id1))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom) (pin-channel-side top))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side bottom))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn) 
          (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type vertical)))
)

(defrule p609
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference ~0))
  (vertical-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?com) (id ?id1))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom) (pin-channel-side bottom))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side top))
  (not (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn)))
  =>
  (assert (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn)
          (seg-id-1 ?id1) (seg-id-2 ?id) (constraint-type vertical)))
)

(defrule p610
  (context (present form-verti))
  (horizontal-s (net-name ?tnn) (min ?min) (max ?max) (com 1) (id ?id))
  (vertical-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (horizontal-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?lr) (id ?id1))
  (last-row ?lr)
  (vertical-s (net-name ?bnn) (com ?vcom))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-x ?vcom) (pin-channel-side top))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-x ?vcom) (pin-channel-side bottom))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn) 
                      (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type vertical)))
)
