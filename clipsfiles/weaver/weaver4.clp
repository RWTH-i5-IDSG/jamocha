

(defrule p611
  (context (present form-verti))
  (vertical-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-y ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))) 
       (pin-channel-side right))
  (vertical-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-y ?vcom) (pin-channel-side left))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)
                      (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type horizontal)))
)

(defrule p612
  (context (present form-verti))
  (vertical-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference 0))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-y ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max)))
       (pin-channel-side left))
  (vertical-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (id ?id1))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-y ?vcom) (pin-channel-side right))
  (not (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn)))
  =>
  (assert (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn)
                      (seg-id-1 ?id1) (seg-id-2 ?id) (constraint-type horizontal)))
)

(defrule p613
  (context (present form-verti))
  (vertical-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference ~0))
  (horizontal-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (vertical-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?com) (id ?id1))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-y ?vcom) (pin-channel-side right))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-y ?vcom) (pin-channel-side left))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)
                      (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type horizontal)))
)

(defrule p614
  (context (present form-verti))
  (vertical-s (net-name ?tnn) (min ?min) (max ?max) (com ?com) (id ?id) (difference ~0))
  (horizontal-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (vertical-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?com) (id ?id1))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-y ?vcom) (pin-channel-side left))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-y ?vcom) (pin-channel-side right))
  (not (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn)))
  =>
  (assert (constraint (net-name-1 ?bnn) (net-name-2 ?tnn) (pin-name-1 ?bpn) (pin-name-2 ?tpn) 
                      (seg-id-1 ?id1) (seg-id-2 ?id) (constraint-type horizontal)))
)

(defrule p615
  (context (present form-verti))
  (vertical-s (net-name ?tnn) (min ?min) (max ?max) (com 1) (id ?id))
  (horizontal-s (net-name ?tnn) (com ?vcom&:(and (>= ?vcom ?min) (<= ?vcom ?max))))
  (vertical-s (net-name ?bnn&~?tnn) (min ?qw74&:(<= ?qw74 ?vcom)) (max ?qw62&:(>= ?qw62 ?vcom)) (com ?lc) (id ?id1))
  (last-col ?lc)
  (horizontal-s (net-name ?bnn) (com ?vcom))
  (pin (net-name ?tnn) (pin-name ?tpn) (pin-y ?vcom) (pin-channel-side right))
  (pin (net-name ?bnn) (pin-name ?bpn) (pin-y ?vcom) (pin-channel-side left))
  (not (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)))
  =>
  (assert (constraint (net-name-1 ?tnn) (net-name-2 ?bnn) (pin-name-1 ?tpn) (pin-name-2 ?bpn)
                      (seg-id-1 ?id) (seg-id-2 ?id1) (constraint-type horizontal)))
)

(defrule p616
  (context (present form-verti))
  (constraint (constraint-type vertical) (net-name-1 ?tnn) (net-name-2 ?bnn) 
              (seg-id-1 ?tid) (seg-id-2 ?bid))
  (constraint (constraint-type vertical) (net-name-1 ?nn1) (net-name-2 ?tnn) 
              (seg-id-1 ?id1) (seg-id-2 ?tid2))
  (not (vertical-cycle ?tnn ?tid ?tid2))
  =>
  (assert (vertical-cycle ?tnn ?tid ?tid2))
)

(defrule p617
  (context (present form-verti))
  (constraint (constraint-type horizontal) (net-name-1 ?tnn) 
              (net-name-2 ?bnn) (seg-id-1 ?tid) (seg-id-2 ?bid))
  (constraint (constraint-type horizontal) (net-name-1 ?nn1) 
              (net-name-2 ?tnn) (seg-id-1 ?id1) (seg-id-2 ?tid2))
  (not (horizontal-cycle ?tnn ?tid ?tid2))
  =>
  (assert (horizontal-cycle ?tnn ?tid ?tid2))
)

(defrule p618
  (context (present remove-cycle))
  ?h <- (horizontal-cycle ?nn)
  (not (constraint (constraint-type horizontal) (net-name-1 ?nn)))
  (not (constraint (constraint-type horizontal) (net-name-2 ?nn)))
  =>
  (retract ?h)
)

(defrule p619
  (context (present remove-cycle))
  ?h <- (horizontal-cycle ?nn)
  (not (constraint (constraint-type horizontal) (net-name-2 ?nn)))
  =>
  (retract ?h)
)

(defrule p620
  (context (present remove-cycle))
  ?h <- (horizontal-cycle ?nn)
  (not (constraint (constraint-type horizontal) (net-name-1 ?nn)))
  =>
  (retract ?h)
)

(defrule p621
  (context (present remove-cycle))
  ?h <- (vertical-cycle ?nn ? ?)
  (not (constraint (constraint-type vertical) (net-name-1 ?nn)))
  (not (constraint (constraint-type vertical) (net-name-2 ?nn)))
  =>
  (retract ?h)
)

(defrule p622
  (context (present remove-cycle))
  ?h <- (vertical-cycle ?nn ? ?)
  (not (constraint (constraint-type vertical) (net-name-2 ?nn)))
  =>
  (retract ?h)
)

(defrule p623
  (context (present remove-cycle))
  ?h <- (vertical-cycle ?nn ? ?)
  (not (constraint (constraint-type vertical) (net-name-1 ?nn)))
  =>
  (retract ?h)
)

(defrule p624
  ?c <- (context (present remove-cycle))
  =>
  (retract ?c)
)



(defrule p625
  (context (present remove-cycle))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?min1) (max ?min) (id ?id2))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side top))
  (not (vertical (status nil) (net-name ~?nn&~nil) (com ?max)))
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

(defrule p626
  (context (present remove-cycle))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?max1) (id ?id2))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side bottom))
  (not (vertical (status nil) (net-name ~?nn&~nil) (com ?min)))
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

(defrule p627
  (context (present remove-cycle))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?min1) (max ?min) (id ?id2))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side bottom))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side top))
  (not (vertical (status nil) (net-name ~?nn&~nil) (com ?max)))
  (not (pin (net-name ?nn) (pin-x ?qw55&:(and (< ?qw55 ?max) (> ?qw55 ?min)))))
  (last-row ?lr)
  (not (horizontal-s (net-name ?nn) (id ?id1)
                     (com ?qw56&:(and (> ?qw56 1) (< ?qw56 ?lr)))))
  (not (horizontal-s (net-name ?nn) (id ?id2) 
                     (com ?qw57&:(and (> ?qw57 1) (< ?qw57 ?lr)))))
  =>
  (retract ?h1)
  (assert (vertical-s (net-name ?nn) (min 1) (max ?lr) (com ?max) 
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
  (modify ?h2 (max ?max))
)

(defrule p628
  (context (present remove-cycle))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (id ?id1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?max1) (id ?id2))
  (pin (net-name ?nn) (pin-x ?max) (pin-channel-side top))
  (pin (net-name ?nn) (pin-x ?min) (pin-channel-side bottom))
  (not (vertical (status nil) (net-name ~?nn&~nil) (com ?min)))
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

(defrule p629
  (context (present remove-cycle))
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (com 1))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?hmin) (<= ?gx ?hmax))) 
      (grid-y ?gy) (came-from south))
  (not (ff (net-name ?nn) (grid-x ~?gx) (grid-y ?qz20&:(< ?qz20 ?gy))))
  (not (vertical-s (net-name ?nn) (min 1) (max ?qw86&:(> ?qw86 ?gy)) (com ?gx)))
  =>
  (assert (vertical-s (net-name ?nn) (min 1) (max =(+ ?gy 1)) (com ?gx) 
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
)

(defrule p630
  (context (present remove-cycle))
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (com 1))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?hmin) (<= ?gx ?hmax))) 
      (grid-y ?gy) (came-from south))
  (not (ff (net-name ?nn) (grid-x ~?gx) (grid-y ?qw74&:(<= ?qw74 ?gy))))
  ?v <- (vertical-s (net-name ?nn) (min 1) (max ?qw75&:(<= ?qw75 ?gy)) (com ?gx))
  =>
  (modify ?v (max =(+ ?gy 1)))
)

(defrule p631
  (context (present remove-cycle))
  (last-row ?lr)
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (com ?lr))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?hmin) (<= ?gx ?hmax))) 
      (grid-y ?gy) (came-from north))
  (not (ff (net-name ?nn) (grid-x ~?gx) (grid-y ?qw86&:(> ?qw86 ?gy))))
  (not (vertical-s (net-name ?nn) (min ?qz20&:(< ?qz20 ?gy)) (max ?lr) (com ?gx)))
  =>
  (assert (vertical-s (net-name ?nn) (min =(- ?gy 1)) (max ?lr) (com ?gx) 
                      (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
)

(defrule p632
  (context (present remove-cycle))
  (last-row ?lr)
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (com ?lr))
  (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?hmin) (<= ?gx ?hmax))) 
      (grid-y ?gy) (came-from north))
  (not (ff (net-name ?nn) (grid-x ~?gx) (grid-y ?qw62&:(>= ?qw62 ?gy))))
  ?v <- (vertical-s (net-name ?nn) (min ?qw63&:(>= ?qw63 ?gy)) (max ?lr) (com ?gx))
  =>
  (modify ?v (min =(- ?gy 1)))
)

(defrule p633
  (context (present remove-cycle))
  (vertical-s (net-name ?nn) (min ?vmin) (max ?vmax) (com 1))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy&:(and (>= ?gy ?vmin) (<= ?gy ?vmax))) 
      (came-from west))
  (not (ff (net-name ?nn) (grid-x ?qz20&:(< ?qz20 ?gx)) (grid-y ~?gy)))
  (not (horizontal-s (net-name ?nn) (min 1) (max ?qw86&:(> ?qw86 ?gx)) (com ?gy)))
  =>
  (assert (horizontal-s (net-name ?nn) (min 1) (max =(+ ?gx 1)) (com ?gy) 
                        (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
)

(defrule p634
  (context (present remove-cycle))
  (vertical-s (net-name ?nn) (min ?vmin) (max ?vmax) (com 1))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy&:(and (>= ?gy ?vmin) (<= ?gy ?vmax))) 
      (came-from west))
  (not (ff (net-name ?nn) (grid-x ?qw74&:(<= ?qw74 ?gx)) (grid-y ~?gy)))
  ?h <- (horizontal-s (net-name ?nn) (min 1) (max ?qw75&:(<= ?qw75 ?gx)) (com ?gy))
  =>
  (modify ?h (max =(+ ?gx 1)))
)

(defrule p635
  (context (present remove-cycle))
  (last-col ?lc)
  (vertical-s (net-name ?nn) (min ?vmin) (max ?vmax) (com ?lc))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy&:(and (>= ?gy ?vmin) (<= ?gy ?vmax))) 
      (came-from east))
  (not (ff (net-name ?nn) (grid-x ?qw86&:(> ?qw86 ?gx)) (grid-y ~?gy)))
  (not (horizontal-s (net-name ?nn) (min ?qz20&:(< ?qz20 ?gx)) (max ?lc) (com ?gy)))
  =>
  (assert (horizontal-s (net-name ?nn) (min =(- ?gx 1)) (max ?lc) (com ?gy) 
                        (id =(gensym)) (difference 0) (absolute 0) (sum 2)))
)

(defrule p636
  (context (present remove-cycle))
  (last-col ?lc)
  (vertical-s (net-name ?nn) (min ?vmin) (max ?vmax) (com ?lc))
  (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy&:(and (>= ?gy ?vmin) (<= ?gy ?vmax))) 
      (came-from east))
  (not (ff (net-name ?nn) (grid-x ?qw62&:(>= ?qw62 ?gx)) (grid-y ~?gy)))
  ?h <- (horizontal-s (net-name ?nn) (min ?qw63&:(>= ?qw63 ?gx)) (max ?lc) (com ?gy))
  =>
  (modify ?h (min =(- ?gx 1)))
)

(defrule p637
  (context (present remove-cycle))
  (last-row ?lr)
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (sum 2) (difference 0))
  ?v <- (vertical-s (net-name ?nn) (min 1) (max ?lr&~2)
                    (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))))
  (pin (net-name ?nn) (pin-x ?vcom) (pin-channel-side top))
  (ff (net-name ~?nn) (grid-x ?vcom) (came-from south))
  =>
  (modify ?v (min =(- ?lr 1)))
)

(defrule p638
  (context (present remove-cycle))
  (last-row ?lr)
  (horizontal-s (net-name ?nn) (min ?hmin) (max ?hmax) (sum 2) (difference 0))
  ?v <- (vertical-s (net-name ?nn) (min 1) (max ?lr&~2) 
                    (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))))
  (pin (net-name ?nn) (pin-x ?vcom) (pin-channel-side bottom))
  (ff (net-name ~?nn) (grid-x ?vcom) (came-from north))
  =>
  (modify ?v (max 2))
)

(defrule p639
  (context (present remove-cycle))
  (last-col ?lc)
  (vertical-s (net-name ?nn) (min ?hmin) (max ?hmax) (sum 2) (difference 0))
  ?v <- (horizontal-s (net-name ?nn) (min 1) (max ?lc&~2) 
                      (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))))
  (pin (net-name ?nn) (pin-x ?vcom) (pin-channel-side right))
  (ff (net-name ~?nn) (grid-x ?vcom) (came-from west))
  =>
  (modify ?v (min =(- ?lc 1)))
)


(defrule p640
  (context (present remove-cycle))
  (last-col ?lc)
  (vertical-s (net-name ?nn) (min ?hmin) (max ?hmax) (sum 2) (difference 0))
  ?v <- (horizontal-s (net-name ?nn) (min 1) (max ?lc&~2) 
                      (com ?vcom&:(and (>= ?vcom ?hmin) (<= ?vcom ?hmax))))
  (pin (net-name ?nn) (pin-x ?vcom) (pin-channel-side left))
  (ff (net-name ~?nn) (grid-x ?vcom) (came-from east))
  =>
  (modify ?v (max 2))
)

(defrule p472
  (context (present partial-route))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) 
              (pin-name ?pn) (came-from north))
  ?vs <- (vertical-s (net-name ?nn) (min ?gy1) (max ?gy) (com ?gx))
  ?ff2 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy1) (grid-layer ?lay) 
              (pin-name ?pn1) (came-from south))
  ?v <- (vertical (net-name nil) (min ?gy1) (max ?gy) (com ?gx) (layer ?lay))
  =>
  (modify ?v (net-name ?nn) (pin-name ?pn))
  (retract ?ff1 ?ff2 ?vs)
)

(defrule p473
  (context (present partial-route))
  ?ff1 <- (ff (net-name ?nn) (grid-x ?gx) (grid-y ?gy) (grid-layer ?lay) 
              (pin-name ?pn) (came-from east))
  ?vs <- (horizontal-s (net-name ?nn) (min ?gx1) (max ?gx) (com ?gy))
  ?ff2 <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy) (grid-layer ?lay) 
              (pin-name ?pn1) (came-from west))
  ?v <- (horizontal (net-name nil) (min ?gx1) (max ?gx) (com ?gy) (layer ?lay))
  =>
  (modify ?v (net-name ?nn) (pin-name ?pn))
  (retract ?ff1 ?ff2 ?vs)
)

(defrule p474
  (context (present partial-route))
  ?v1 <-(vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?vcom))
  ?h <- (horizontal-s (net-name ?nn) (min ?vcom) (max ?hmax) (com ?min) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?vmin) (max ?min) (com ?hmax) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmin) (came-from east))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?max) (came-from north))
  =>
  (retract ?v2)
  (modify ?h (com ?vmin))
  (modify ?v1 (min ?vmin))
)

(defrule p475
  (context (present partial-route))
  ?h <- (horizontal-s (net-name ?nn) (min ?vcom) (max ?hmax) (com ?min)
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?vmin) (max ?min) (com ?hmax) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmin) (came-from east))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?min) (came-from north))
  =>
  (modify ?v2 (com ?vcom))
  (modify ?h (com ?vmin))
)

(defrule p476
  (context (present partial-route))
  ?v1 <-(vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?vcom))
  ?h <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?vcom) (com ?min) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?vmin) (max ?min) (com ?hmin) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmin) (came-from west))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?max) (came-from north))
  =>
  (retract ?v2)
  (modify ?h (com ?vmin))
  (modify ?v1 (min ?vmin))
)

(defrule p477
  (context (present partial-route))
  ?h <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?vcom) (com ?min) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?vmin) (max ?min) (com ?hmin) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmin) (came-from west))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?min) (came-from north))
  =>
  (modify ?v2 (com ?vcom))
  (modify ?h (com ?vmin))
)

(defrule p478
  (context (present partial-route))
  ?v1 <-(vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?vcom))
  ?h <- (horizontal-s (net-name ?nn) (min ?vcom) (max ?hmax) (com ?max)
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?max) (max ?vmax) (com ?hmax) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmax) (came-from east))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?min) (came-from south))
  =>
  (retract ?v2)
  (modify ?h (com ?vmax))
  (modify ?v1 (max ?vmax))
)

(defrule p479
  (context (present partial-route))
  ?h <- (horizontal-s (net-name ?nn) (min ?vcom) (max ?hmax) (com ?max) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?max) (max ?vmax) (com ?hmax) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmax) (came-from east))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?max) (came-from south))
  =>
  (modify ?v2 (com ?vcom))
  (modify ?h (com ?vmax))
)

(defrule p480
  (context (present partial-route))
  ?v1 <-(vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?vcom))
  ?h <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?vcom) (com ?max) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?max) (max ?vmax) (com ?hmin) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmax) (came-from west))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?min) (came-from south))
  =>
  (retract ?v2)
  (modify ?h (com ?vmax))
  (modify ?v1 (max ?vmax))
)

(defrule p481
  (context (present partial-route))
  ?h <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?vcom) (com ?max) 
                      (top-count 1) (bot-count 1))
  ?v2 <- (vertical-s (net-name ?nn) (min ?max) (max ?vmax) (com ?hmin) 
                     (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmax) (came-from west))
  (ff (net-name ?nn) (grid-x ?vcom) (grid-y ?max) (came-from south))
  =>
  (modify ?v2 (com ?vcom))
  (modify ?h (com ?vmax))
)

(defrule p482
  (context (present partial-route))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom))
  ?v <- (vertical-s (net-name ?nn) (min ?hcom) (max ?vmax) (com ?min) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?min) (com ?vmax) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmax) (came-from north))
  (ff (net-name ?nn) (grid-x ?max) (grid-y ?hcom) (came-from east))
  =>
  (retract ?h2)
  (modify ?v (com ?hmin))
  (modify ?h1 (min ?hmin))
)

(defrule p483
  (context (present partial-route))
  ?v <- (vertical-s (net-name ?nn) (min ?hcom) (max ?vmax) (com ?min) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?min) (com ?vmax) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmax) (came-from north))
  (ff (net-name ?nn) (grid-x ?min) (grid-y ?hcom) (came-from east))
  =>
  (modify ?h2 (com ?hcom))
  (modify ?v (com ?hmin))
)

(defrule p484
  (context (present partial-route))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom))
  ?v <- (vertical-s (net-name ?nn) (min ?hcom) (max ?vmax) (com ?max) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?hmax) (com ?vmax)
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmax) (came-from north))
  (ff (net-name ?nn) (grid-x ?min) (grid-y ?hcom) (came-from west))
  =>
  (retract ?h2)
  (modify ?v (com ?hmax))
  (modify ?h1 (max ?hmax))
)

(defrule p485
  (context (present partial-route))
  ?v <- (vertical-s (net-name ?nn) (min ?hcom) (max ?vmax) (com ?max)
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?hmax) (com ?vmax)
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmax) (came-from north))
  (ff (net-name ?nn) (grid-x ?max) (grid-y ?hcom) (came-from west))
  =>
  (modify ?h2 (com ?hcom))
  (modify ?v (com ?hmax))
)



(defrule p486
  (context (present partial-route))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin) (max ?hcom) (com ?min) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?min) (com ?vmin) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmin) (came-from south))
  (ff (net-name ?nn) (grid-x ?max) (grid-y ?hcom) (came-from east))
  =>
  (retract ?h2)
  (modify ?v (com ?hmin))
  (modify ?h1 (min ?hmin))
)

(defrule p487
  (context (present partial-route))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin) (max ?hcom) (com ?min) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?hmin) (max ?min) (com ?vmin) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmin) (grid-y ?vmin) (came-from south))
  (ff (net-name ?nn) (grid-x ?min) (grid-y ?hcom) (came-from east))
  =>
  (modify ?h2 (com ?hcom))
  (modify ?v (com ?hmin))
)

(defrule p488
  (context (present partial-route))
  ?h1 <- (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?hcom))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin) (max ?hcom) (com ?max)
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?hmax) (com ?vmin) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmin) (came-from south))
  (ff (net-name ?nn) (grid-x ?min) (grid-y ?hcom) (came-from west))
  =>
  (retract ?h2)
  (modify ?v (com ?hmax))
  (modify ?h1 (max ?hmax))
)

(defrule p489
  (context (present partial-route))
  ?v <- (vertical-s (net-name ?nn) (min ?vmin) (max ?hcom) (com ?max) 
                    (top-count 1) (bot-count 1))
  ?h2 <- (horizontal-s (net-name ?nn) (min ?max) (max ?hmax) (com ?vmin) 
                       (top-count 1) (bot-count 1))
  (ff (net-name ?nn) (grid-x ?hmax) (grid-y ?vmin) (came-from south))
  (ff (net-name ?nn) (grid-x ?max) (grid-y ?hcom) (came-from west))
  =>
  (modify ?h2 (com ?hcom))
  (modify ?v (com ?hmax))
)

(defrule p186
  (context (present extend-ff))
  (horizontal-s (net-name ?nn) (difference ?d&:(< ?d 0)))
  (not (horizontal-s (difference ?qz20&:(< ?qz20 ?d))))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay1) 
             (pin-name ?pn) (came-from south))
  (not (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?qz21&:(< ?qz21 ?gy1))))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy1))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx2)) (max ?qw86&:(> ?qw86 ?gx1)) (com ?gy1)))
  (not (horizontal (net-name nil) (min ?gx2) (max ?qw87&:(> ?qw87 ?gx1)) (com ?gy1) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz23&:(< ?qz23 ?gx2)) (max ?gx1) (com ?gy1) (max-net nil)))
  ?v <- (vertical (net-name nil) (max ?max&:(> ?max ?gy1)) (min ?qz37&:(and (< ?qz37 ?max) (<= ?qz37 ?gy1))) 
                  (com ?gx1) (layer ?lay1) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (congestion (direction row) (coordinate ?gy2) (como ?gy1))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx1) (layer ?lay1)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw63&:(>= ?qw63 ?gx1)) (com ?gy2) (layer ?lay1)))
  (not (extended-ff ?nn bottom))
  (not (vertical-cycle ?nn ? ?))
  =>
  (modify ?v (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy2) (min ?gy1) (layer ?lay1) (com ?gx1) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
)

(defrule p187
  (context (present extend-ff))
  (horizontal-s (net-name ?nn) (difference ?d&:(< ?d 0)))
  (not (horizontal-s (difference ?qz20&:(< ?qz20 ?d))))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay1) (pin-name ?pn) (came-from south))
  (not (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?qz21&:(< ?qz21 ?gy1))))
  (ff (net-name ?nn) (grid-x ?gx2&:(> ?gx2 ?gx1)) (grid-y ?gy1))
  (not (horizontal (net-name nil) (min ?qz22&:(< ?qz22 ?gx1)) (max ?qw86&:(> ?qw86 ?gx2)) (com ?gy1)))
  (not (horizontal (net-name nil) (min ?gx1) (max ?qw87&:(> ?qw87 ?gx2)) (com ?gy1) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz23&:(< ?qz23 ?gx1)) (max ?gx2) (com ?gy1) (max-net nil)))
  ?v <- (vertical (net-name nil) (max ?max&:(> ?max ?gy1)) (min ?qz37&:(and (< ?qz37 ?max) (<= ?qz37 ?gy1))) (com ?gx1) (layer ?lay1) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (congestion (direction row) (coordinate ?gy2) (como ?gy1))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx1) (layer ?lay1)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw63&:(>= ?qw63 ?gx1)) (com ?gy2) (layer ?lay1)))
  (not (extended-ff ?nn bottom))
  (not (vertical-cycle ?nn ? ?))
  =>
  (modify ?v (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy2) (min ?gy1) (layer ?lay1) (com ?gx1) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
)

(defrule p188
  (context (present extend-ff))
  (horizontal-s (net-name ?nn) (difference ?d&:(< ?d 0)))
  (not (horizontal-s (difference ?qz20&:(< ?qz20 ?d))))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay1) (pin-name ?pn) (came-from south))
  (vertical-s (net-name ?nn) (min ?qw74&:(<= ?qw74 ?gy1)) (max ?qw86&:(> ?qw86 ?gy1)) (com ?gx1))
  (not (ff (net-name ?nn) (grid-x ~?gx1) (grid-y ?qw75&:(<= ?qw75 ?gy1))))
  ?v <- (vertical (net-name nil) (max ?max&:(> ?max ?gy1)) (min ?qz37&:(and (< ?qz37 ?max) (<= ?qz37 ?gy1))) (com ?gx1) (layer ?lay1) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (congestion (direction row) (coordinate ?gy2) (como ?gy1))
  (not (vertical-s (net-name ~?nn) (min ?qw76&:(<= ?qw76 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx1)))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw77&:(<= ?qw77 ?gy2)) (max ?qw63&:(>= ?qw63 ?gy2)) (com ?gx1) (layer ?lay1)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw64&:(>= ?qw64 ?gx1)) (com ?gy2) (layer ?lay1)))
  (not (extended-ff ?nn bottom))
  (not (vertical-cycle ?nn ? ?))
  =>
  (assert (extended-ff ?nn bottom))
  (modify ?v (min ?gy2) (min-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy2) (min ?gy1) (layer ?lay1) (com ?gx1) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
)

(defrule p189
  (context (present extend-ff))
  (horizontal-s (net-name ?nn) (difference ?d&:(> ?d 0)))
  (not (horizontal-s (difference ?qw86&:(> ?qw86 ?d))))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx1) (grid-y ?gy1) (grid-layer ?lay1) (pin-name ?pn) (came-from north))
  (not (ff (net-name ?nn) (grid-x ?garb1) (grid-y ?qw87&:(> ?qw87 ?gy1))))
  (ff (net-name ?nn) (grid-x ?gx2&:(< ?gx2 ?gx1)) (grid-y ?gy1))
  (not (horizontal (net-name nil) (min ?qz20&:(< ?qz20 ?gx2)) (max ?qw88&:(> ?qw88 ?gx1)) (com ?gy1)))
  (not (horizontal (net-name nil) (min ?gx2) (max ?qw89&:(> ?qw89 ?gx1)) (com ?gy1) (min-net nil)))
  (not (horizontal (net-name nil) (min ?qz21&:(< ?qz21 ?gx2)) (max ?gx1) (com ?gy1) (max-net nil)))
  ?v <- (vertical (net-name nil) (max ?max&:(>= ?max ?gy1)) (min ?qz36&:(and (< ?qz36 ?max) (< ?qz36 ?gy1))) (com ?gx1) (layer ?lay1) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (congestion (direction row) (coordinate ?gy1) (como ?gy2))
  (not (vertical (status nil) (net-name ~nil&~?nn) (min ?qw74&:(<= ?qw74 ?gy2)) (max ?qw62&:(>= ?qw62 ?gy2)) (com ?gx1) (layer ?lay1)))
  (not (horizontal (status nil) (net-name ~nil&~?nn) (min ?qw34&:(<= ?qw34 ?gx1)) (max ?qw63&:(>= ?qw63 ?gx1)) (com ?gy2) (layer ?lay1)))
  (not (extended-ff ?nn top))
  (not (vertical-cycle ?nn ? ?))
  =>
  (modify ?v (max ?gy2) (max-net ?nn))
  (assert (vertical (net-name ?nn) (max ?gy1) (min ?gy2) (layer ?lay1) (com ?gx1) (commo ?cmo) (compo ?cpo) (pin-name ?pn)))
  (modify ?ff (grid-y ?gy2) (can-chng-layer nil))
)
