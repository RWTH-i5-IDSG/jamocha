
(defrule p327
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) 
      (grid-y ?y))
  (horizontal-s (net-name ?nn) (min ?hmin&:(<= ?hmin ?gx)) 
                (max ?hmax&:(and (>= ?hmax ?gx) (> ?hmax ?min))) (com 1) (id ?id))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ~?id)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?qx1&:(and (>= ?qx1 ?hmin) (<= ?qx1 ?hmax)))
       (pin-channel-side bottom))
  (constraint (constraint-type vertical) (seg-id-1 ?id))
  (total (net-name ~?nn))
  (not (total-verti ?nn ? ? ?))
  =>
  (retract ?t)
)

(defrule p328
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) 
      (grid-y ?y))
  (horizontal-s (net-name ?nn) (min ?hmin&:(and (<= ?hmin ?gx) (< ?hmin ?max))) 
                (max ?hmax&:(>= ?hmax ?gx)) (com ?lr) (id ?id))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?lr) (id ~?id)))
  (last-row ?lr)
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?qx1&:(and (>= ?qx1 ?hmin) (<= ?qx1 ?hmax)))
       (pin-channel-side top))
  (constraint (constraint-type vertical) (seg-id-2 ?id))
  (total (net-name ~?nn))
  (not (total-verti ?nn ? ? ?))
  =>
  (retract ?t)
)

(defrule p329
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) 
      (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(and (>= ?vmax ?gy) (> ?vmax ?min))) 
                (com 1) (id ?id))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ~?id)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-y ?qz1&:(and (>= ?qz1 ?vmin) (<= ?qz1 ?vmax))) (pin-channel-side left))
  (constraint (constraint-type horizontal) (seg-id-1 ?id))
  (total (net-name ~?nn))
  (not (total-verti ?nn ? ? ?))
  =>
  (retract ?t)
)

(defrule p330
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) 
      (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min ?vmin&:(and (<= ?vmin ?gy) (< ?vmin ?max)))
              (max ?vmax&:(>= ?vmax ?gy)) (com ?lc) (id ?id))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?lc) (id ~?id)))
  (last-col ?lc)
  (pin (net-name ?nn) (pin-name ?pn) (pin-y ?qz1&:(and (>= ?qz1 ?vmin) (<= ?qz1 ?vmax))) 
       (pin-channel-side right))
  (constraint (constraint-type horizontal) (seg-id-2 ?id))
  (total (net-name ~?nn))
  (not (total-verti ?nn ? ? ?))
  =>
  (retract ?t)
)

(defrule p331
  (declare (salience -10))
  ?c <- (context (present modify-total))
  =>
  (modify ?c (present delete-total))
)

(defrule p332
  (declare (salience -10))
  ?c <- (context (present delete-total))
  (not (total))
  =>
  (retract ?c)
  (assert (context (previous find-no-of-pins-on-a-row-col)))
)

(defrule p333
  (declare (salience -10))
  ?c <- (context (present extend-total))
  (last-row ?lr)
  =>
  (assert (maximum-total 0 0 0 1 0 ?lr 0))
  (assert (merge-direction left))
  (modify ?c (present merge))
  (assert (eliminate-total))
)

(defrule p334
  (declare (salience -10))
  ?c <- (context (present delete-total))
  =>
  (modify ?c (present extend-total))
)

(defrule p335
  (context (present modify-total))
  (total (net-name ?nn) (row-col row) (coor ?y) (cong ?qx2)
         (min-xy ?min) (max-xy ?max) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) 
      (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?y))
  (horizontal-s (net-name ?nn) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(>= ?hmax ?gx))
                (com 1) (id ?id) (absolute ?a) (sum ?s))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ~?id)))
  (not (total (net-name ?nn) (row-col row) (coor ?y) 
              (min-xy ?hmin) (max-xy ?hmax) (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?qx1&:(and (>= ?qx1 ?hmin) (<= ?qx1 ?hmax)))
       (pin-channel-side bottom))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?y) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?hmin) (max-xy ?hmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?hmin ?hmax)))
)

(defrule p336
  (context (present modify-total))
  (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max) 
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) 
      (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?y))
  (horizontal-s (net-name ?nn) (min ?hmin&:(<= ?hmin ?gx)) (max ?hmax&:(>= ?hmax ?gx))
                (com ?lr) (id ?id) (absolute ?a) (sum ?s))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?lr) (id ~?id)))
  (not (total (net-name ?nn) (row-col row) (coor ?y) 
              (min-xy ?hmin) (max-xy ?hmax) (last-pin checked)))
  (last-row ?lr)
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?qx1&:(and (>= ?qx1 ?hmin) (<= ?qx1 ?hmax)))
       (pin-channel-side top))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?y) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?hmin) (max-xy ?hmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?hmin ?hmax)))
)

(defrule p337
  (context (present modify-total))
  (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max)
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(>= ?vmax ?gy)) 
              (com 1) (id ?id) (absolute ?a) (sum ?s))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ~?id)))
  (not (total (net-name ?nn) (row-col col) (coor ?x) 
              (min-xy ?vmin) (max-xy ?vmax) (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-y ?qx1&:(and (>= ?qx1 ?vmin) (<= ?qx1 ?vmax))) 
       (pin-channel-side left))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?x) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?vmin) (max-xy ?vmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?vmin ?vmax)))
)

(defrule p338
  (context (present modify-total))
  (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max)
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gy)) (max ?vmax&:(>= ?vmax ?gy))
              (com ?lc) (id ?id) (absolute ?a) (sum ?s))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?lc) (id ~?id)))
  (not (total (net-name ?nn) (row-col col) (coor ?x) 
              (min-xy ?vmin) (max-xy ?vmax) (last-pin checked)))
  (last-col ?lc)
  (pin (net-name ?nn) (pin-name ?pn) (pin-y ?qx1&:(and (>= ?qx1 ?vmin) (<= ?qx1 ?vmax)))
       (pin-channel-side right))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?x) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?vmin) (max-xy ?vmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?vmin ?vmax)))
)

(defrule p339
  (context (present modify-total))
  (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max)
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) (grid-y ?y))
  (horizontal-s (net-name ?nn) (min 1) (max ?hmax&:(>= ?hmax ?gx))
                (com ?com) (id ?id) (absolute ?a) (sum ?s))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?garb) (id ~?id)))
  (not (total (net-name ?nn) (row-col row) (coor ?y) (min-xy 1) (max-xy ?hmax) (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-x 0) (pin-y ?com) (pin-channel-side left))
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?y) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy 1) (max-xy ?hmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn 1 ?hmax)))
)

(defrule p340
  (context (present modify-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max) 
               (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) 
      (grid-y ?y))
  (horizontal-s (net-name ?nn) (min ?hmin&:(<= ?hmin ?gx)) 
                (max ?lc) (com ?com) (id ?id) (absolute ?a) (sum ?s))
  (not (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?garb) (id ~?id)))
  (not (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?hmin) 
              (max-xy ?lc) (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-y ?com) (pin-channel-side right))
  (last-col ?lc)
  =>
  (assert (total (net-name ?nn) (row-col row) (coor ?y) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?hmin) (max-xy ?lc)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?hmin ?lc)))
)

(defrule p341
  (context (present modify-total))
  (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max) 
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) 
      (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min 1) (max ?vmax&:(>= ?vmax ?gy)) 
              (com ?com) (id ?id) (absolute ?a) (sum ?s))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?garb) (id ~?id)))
  (not (total (net-name ?nn) (row-col col) (coor ?x) (min-xy 1) (max-xy ?vmax) 
       (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?com) (pin-channel-side bottom))
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?x) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy 1) (max-xy ?vmax)  
                 (last-pin checked) (last-xy ?id) (nets ?nn 1 ?vmax)))
)

(defrule p342
  (context (present modify-total))
  (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max) 
         (cong ?qx2) (last-pin ~checked))
  (ff (net-name ?nn) (pin-name ?pn) (grid-x ?x) 
      (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))))
  (vertical-s (net-name ?nn) (min ?vmin&:(<= ?vmin ?gy)) 
              (max ?lr) (com ?com) (id ?id) (absolute ?a) (sum ?s))
  (not (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?garb) (id ~?id)))
  (not (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?vmin) (max-xy ?lr) 
       (last-pin checked)))
  (pin (net-name ?nn) (pin-name ?pn) (pin-x ?com) (pin-channel-side top))
  (last-row ?lr)
  =>
  (assert (total (net-name ?nn) (row-col col) (coor ?x) (cong ?qx2)
                 (level-pins ?a) (total-pins ?s) (min-xy ?vmin) (max-xy ?lr)  
                 (last-pin checked) (last-xy ?id) (nets ?nn ?vmin ?lr)))
)

(defrule p343
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?y) 
               (min-xy ?min) (max-xy ?max) (last-pin ~checked))
  (total (net-name ?nn) (row-col ?rc) (coor ?y) (last-pin checked))
  =>
  (retract ?t)
)

(defrule p344
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?y) 
               (min-xy ?min) (max-xy ?max) (last-pin ~checked))
  =>
  (retract ?t)
)

(defrule p345
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?y) 
               (min-xy ?min) (max-xy ?max) (last-pin ~checked))
  (net (net-name ?nn) (net-no-of-pins 2))
  =>
  (modify ?t (last-pin checked) (nets ?nn ?min ?max))
)

(defrule p346
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col ?rc) (coor ?y) 
               (min-xy ?min) (max-xy ?max) (last-pin ~checked))
  (not (total (last-pin checked)))
  =>
  (modify ?t (last-pin checked) (nets ?nn ?min ?max))
)

(defrule p347
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max))
  (last-col ?lc)
  (horizontal-s (net-name ?nn1&~?nn) (min 1) 
                (max ?qz2&:(and (>= ?qz2 ?min) (!= ?qz2 ?lc))) (com ?y) 
                (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x 0) (pin-y ?y))
  (not (pin (net-name ?nn) (pin-y ?y) (pin-channel-side right)))
  =>
  (retract ?t)
)

(defrule p348
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?tmin) (max-xy ?max))
  (last-col ?lc)
  (horizontal-s (net-name ?nn1&~?nn) (min 1) (max ?maxs&~?lc) 
                (com ?y) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x 0) (pin-y ?y))
  (vertical (net-name ?nn) (min ?qz4&:(<= ?qz4 ?y)) (max ?qz7&:(>= ?qz7 ?y)) 
            (com ?min&:(and (> ?min 1) (<= ?min ?maxs))))
  (not (vertical (net-name ?nn) (min ?qz5&:(<= ?qz5 ?y)) 
                 (max ?qz8&:(>= ?qz8 ?y)) (com ?qw1&:(< ?qw1 ?min))))
  ?h <- (horizontal (net-name ?nn1) (min 0) (max ?hmax&:(< ?hmax ?min)) 
                    (com ?y) (layer ?lay))
  ?ff <- (ff (net-name ?nn1) (grid-x ?hmax) (grid-y ?y) (grid-layer ?lay) (came-from west))
  ?h1 <- (horizontal (net-name nil) (min ?hmax) (max ?qw2&:(> ?qw2 ?hmax)) 
                     (com ?y) (layer ?lay))
  (not (pin (net-name ?nn) (pin-y ?y)))
  (not (vertical (status nil) (net-name ~?nn1&~nil) (min ?qz6&:(<= ?qz6 ?y)) 
                 (max ?qz9&:(>= ?qz9 ?y)) (com ?min) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn1&~nil) (min ?qw3&:(<= ?qw3 ?min))
                   (max ?qw6&:(>= ?qw6 ?min)) (com ?y) (layer ?lay)))
  =>
  (retract ?t)
  (modify ?h (max ?min))
  (modify ?h1 (min ?min) (min-net ?nn1))
  (modify ?ff (grid-x ?min) (can-chng-layer nil))
)

(defrule p349
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?max))
  (last-col ?lc)
  (horizontal-s (net-name ?nn1&~?nn) (min ?qw7&~1&:(<= ?qw7 ?max)) 
                (max ?lc) (com ?y) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-y ?y) (pin-channel-side right))
  (not (pin (net-name ?nn) (pin-y ?y) (pin-channel-side left)))
  =>
  (retract ?t)
)

(defrule p350
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) (min-xy ?min) (max-xy ?tmax))
  (last-col ?lc)
  (horizontal-s (net-name ?nn1&~?nn) (min ?mins&~1) 
                (max ?lc) (com ?y) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-y ?y) (pin-channel-side right))
  (vertical (net-name ?nn) (min ?qz4&:(<= ?qz4 ?y)) (max ?qz7&:(>= ?qz7 ?y)) 
            (com ?max&:(and (>= ?max ?mins) (< ?max ?lc))))
  (not (vertical (net-name ?nn) (min ?qz5&:(<= ?qz5 ?y)) 
                 (max ?qz8&:(>= ?qz8 ?y)) (com ?qw8&:(> ?qw8 ?max))))
  ?h <- (horizontal (net-name ?nn1) (min ?hmin&:(> ?hmin ?max))
                    (max ?garb) (com ?y) (layer ?lay))
  ?ff <- (ff (net-name ?nn1) (grid-x ?hmin) (grid-y ?y) (grid-layer ?lay) (came-from east))
  ?h1 <- (horizontal (net-name nil) (min ?qw9&:(< ?qw9 ?max)) 
                     (max ?hmin) (com ?y) (layer ?lay))
  (not (pin (net-name ?nn) (pin-y ?y)))
  (not (vertical (status nil) (net-name ~?nn1&~nil) (min ?qz6&:(<= ?qz6 ?y)) 
                 (max ?qz9&:(>= ?qz9 ?y)) (com ?max) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn1&~nil) (min ?qw11&:(<= ?qw11 ?max)) 
                   (max ?qw12&:(>= ?qw12 ?max)) (com ?y) (layer ?lay)))
  =>
  (retract ?t)
  (modify ?h (min ?max))
  (modify ?h1 (max ?max) (max-net ?nn1))
  (modify ?ff (grid-x ?max) (can-chng-layer nil))
)

(defrule p351
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max))
  (last-row ?lr)
  (vertical-s (net-name ?nn1&~?nn) (min 1) 
              (max ?qw15&~?lr&:(>= ?qw15 ?min)) (com ?x) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x ?x) (pin-channel-side bottom))
  (not (pin (net-name ?nn) (pin-x ?x) (pin-channel-side top)))
  =>
  (retract ?t)
)

(defrule p352
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?tmin) (max-xy ?max))
  (last-row ?lr)
  (vertical-s (net-name ?nn1&~?nn) (min 1) (max ?maxs&~?lr) 
              (com ?x) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x ?x) (pin-channel-side bottom))
  (horizontal (net-name ?nn) (min ?qw16&:(<= ?qw16 ?x)) (max ?x)
              (com ?min&:(and (> ?min 1) (<= ?min ?maxs))))
  (not (horizontal (net-name ?nn) (min ?qw17&:(<= ?qw17 ?x))
                   (max ?x) (com ?qw1&:(< ?qw1 ?min))))
  ?v <- (vertical (net-name ?nn1) (min 0) (max ?vmax&:(< ?vmax ?min)) 
                  (com ?x) (layer ?lay))
  ?ff <- (ff (net-name ?nn1) (grid-x ?x) (grid-y ?vmax) (grid-layer ?lay) (came-from south))
  ?v1 <-(vertical (net-name nil) (min ?vmax) (max ?qw19&:(< ?qw19 ?vmax)) (com ?x) (layer ?lay))
  (not (pin (net-name ?nn) (pin-x ?x)))
  (not (vertical (status nil) (net-name ~?nn1&~nil) 
                 (min ?qw3&:(<= ?qw3 ?min)) (max ?qw6&:(>= ?qw6 ?min)) (com ?x)))
  (not (horizontal (status nil) (net-name ~?nn1&~nil) 
                   (min ?qw18&:(<= ?qw18 ?x)) (max ?qw62&:(>= ?qw62 ?x)) (com ?min)))
  =>
  (retract ?t)
  (modify ?v (max ?min))
  (modify ?v1 (min ?min) (min-net ?nn1))
  (modify ?ff (grid-y ?min) (can-chng-layer nil))
)

(defrule p353
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?max))
  (last-row ?lr)
  (vertical-s (net-name ?nn1&~?nn) (min ?qw7&~1&:(<= ?qw7 ?max)) 
              (max ?lr) (com ?x) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x ?x) (pin-channel-side top))
  (not (pin (net-name ?nn) (pin-x ?x) (pin-channel-side bottom)))
  =>
  (retract ?t)
)

(defrule p354
  (context (present delete-total))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) (min-xy ?min) (max-xy ?tmax))
  (last-row ?lr)
  (vertical-s (net-name ?nn1&~?nn) (min ?mins&~1) (max ?lr) 
              (com ?x) (id ?qz3&:(numberp ?qz3)))
  (pin (net-name ?nn1) (pin-x ?x) (pin-channel-side top))
  (horizontal (net-name ?nn) (min ?qw16&:(<= ?qw16 ?x)) 
              (max ?qw20&:(>= ?qw20 ?x)) (com ?max&:(and (>= ?max ?mins) (< ?max ?lr))))
  (not (horizontal (net-name ?nn) (min ?qw17&:(<= ?qw17 ?x)) 
       (max ?qw20&:(>= ?qw20 ?x)) (com ?qw8&:(> ?qw8 ?max))))
  ?v <- (vertical (net-name ?nn1) (min ?vmin&:(> ?vmin ?max)) (max ?garb) (com ?x) (layer ?lay))
  ?ff <- (ff (net-name ?nn1) (grid-x ?x) (grid-y ?vmin) (grid-layer ?lay) (came-from north))
  ?v1 <-(vertical (net-name nil) (min ?qw21&:(< ?qw21 ?vmin)) (max ?vmin) (com ?x) (layer ?lay))
  (not (pin (net-name ?nn) (pin-x ?x)))
  (not (vertical (status nil) (net-name ~?nn1&~nil) (min ?qw11&:(<= ?qw11 ?max)) 
                 (max ?qw12&:(>= ?qw12 ?max)) (com ?x)))
  (not (horizontal (status nil) (net-name ~?nn1&~nil) (min ?qw18&:(<= ?qw18 ?x)) 
                   (max ?qw22&:(>= ?qw22 ?x)) (com ?max)))
  =>
  (retract ?t)
  (modify ?v (min ?max))
  (modify ?v1 (max ?max) (max-net ?nn1))
  (modify ?ff (grid-y ?max) (can-chng-layer nil))
)

(defrule p355
  (context (present extend-total))
  (not (switch-box))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y) 
               (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (last-row ?qw30&:(> ?qw30 ?y))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ?id))
  (not (horizontal (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
                   (max ?qw12&:(>= ?qw12 ?max)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw4&:(<= ?qw4 ?min)) 
                   (max ?qw6&:(>= ?qw6 ?min)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw11&:(<= ?qw11 ?max)) 
                   (max ?qw13&:(>= ?qw13 ?max)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw23&:(>= ?qw23 ?min))
                   (max ?qw24&:(<= ?qw24 ?max)) (com ?y)))
  (horizontal (net-name nil) (min ?qw5&:(<= ?qw5 ?min)) 
              (max ?qw14&:(>= ?qw14 ?max)) (com ?qw25&:(> ?qw25 ?y)))
  (not (ff (net-name ?nn) (grid-x ?qw26&:(and (>= ?qw26 ?min) (<= ?qw26 ?max))) 
           (grid-y ?y) (pin-name ?qw27&:(>= ?qw27 1000))))
  =>
  (modify ?t (coor =(+ ?y 1)))
)


(defrule p356
  (context (present extend-total))
  (not (switch-box))
  ?t <- (total (net-name ?nn) (row-col row) (coor ?y&:(> ?y 0))
               (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?lr) (id ?id))
  (last-row ?lr)
  (not (horizontal (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
                   (max ?qw12&:(>= ?qw12 ?max)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw4&:(<= ?qw4 ?min)) 
                   (max ?qw6&:(>= ?qw6 ?min)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw11&:(<= ?qw11 ?max)) 
                   (max ?qw13&:(>= ?qw13 ?max)) (com ?y)))
  (not (horizontal (net-name ?nn) (min ?qw23&:(>= ?qw23 ?min)) 
                   (max ?qw24&:(<= ?qw24 ?max)) (com ?y)))
  (horizontal (net-name nil) (min ?qw5&:(<= ?qw5 ?min)) 
              (max ?qw14&:(>= ?qw14 ?max)) (com ?qw28&:(< ?qw28 ?y)))
  (not (ff (net-name ?nn) (grid-x ?qw26&:(and (>= ?qw26 ?min) (<= ?qw26 ?max))) (grid-y ?y) 
            (pin-name ?qw27&:(>= ?qw27 1000))))
  =>
  (modify ?t (coor =(- ?y 1)))
)

(defrule p357
  (context (present extend-total))
  (not (switch-box))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x) 
               (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (last-col ?qw30&:(> ?qw30 ?x))
  (vertical-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ?id))
  (not (vertical (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
                 (max ?qw12&:(>= ?qw12 ?max)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw4&:(<= ?qw4 ?min)) 
                 (max ?qw6&:(>= ?qw6 ?min)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw11&:(<= ?qw11 ?max)) 
                 (max ?qw13&:(>= ?qw13 ?max)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw23&:(>= ?qw23 ?min)) 
                 (max ?qw24&:(<= ?qw24 ?max)) (com ?x)))
  (vertical (net-name nil) (min ?qw5&:(<= ?qw5 ?min))
            (max ?qw14&:(>= ?qw14 ?max)) (com ?qw29&:(> ?qw29 ?x)))
  (not (ff (net-name ?nn) (grid-x ?x) (grid-y ?qw32&:(and (>= ?qw32 ?min) (<= ?qw32 ?max)))
           (pin-name ?qw31&:(>= ?qw31 1000))))
  =>
  (modify ?t (coor =(+ ?x 1)))
)

(defrule p358
  (context (present extend-total))
  (not (switch-box))
  ?t <- (total (net-name ?nn) (row-col col) (coor ?x&:(> ?x 0))
               (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?lc) (id ?id))
  (last-col ?lc)
  (not (vertical (net-name nil) (min ?qw3&:(<= ?qw3 ?min))
                 (max ?qw12&:(>= ?qw12 ?max)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw4&:(<= ?qw4 ?min)) 
                 (max ?qw6&:(>= ?qw6 ?min)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw11&:(<= ?qw11 ?max)) 
                 (max ?qw13&:(>= ?qw13 ?max)) (com ?x)))
  (not (vertical (net-name ?nn) (min ?qw23&:(>= ?qw23 ?min)) 
                 (max ?qw24&:(<= ?qw24 ?max)) (com ?x)))
  (vertical (net-name nil) (min ?qw5&:(<= ?qw5 ?min)) 
            (max ?qw14&:(>= ?qw14 ?max)) (com ?qw33&:(< ?qw33 ?x)))
  (not (ff (net-name ?nn) (grid-x ?x) (grid-y ?qw34&:(and (>= ?qw34 ?min) (<= ?qw34 ?max))) 
           (pin-name ?qw31&:(>= ?qw31 1000))))
  =>
  (modify ?t (coor =(- ?x 1)))
)

(defrule p359
  (context (present extend-total))
  (total (net-name ?nn) (row-col row) (coor ?y) 
         (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ?id))
  (horizontal (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
              (max ?qw12&:(>= ?qw12 ?max)) (com ?y))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) 
             (grid-y ?gy&:(< ?gy ?y)) (grid-layer ?lay) (pin-name ?pn))
  ?v <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?gy)) 
                  (max ?vmax&:(>= ?vmax ?y)) (com ?gx) (layer ?lay) (compo ?cpo) 
                  (commo ?cmo) (max-net ?mn))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qz4&:(<= ?qz4 ?y)) 
                 (max ?qz7&:(>= ?qz7 ?y)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) 
                   (min ?qw30&:(<= ?qw30 ?gx)) (max ?qw44&:(>= ?qw44 ?gx))
                   (com ?y) (layer ?lay)))
  =>
  (assert (vertical (min ?y) (max ?vmax) (com ?gx) (layer ?lay) 
                    (compo ?cpo) (commo ?cmo) (min-net ?nn) (max-net ?mn)))
  (modify ?v (max ?gy) (max-net ?nn))
  (assert (vertical (min ?gy) (max ?y) (com ?gx) (layer ?lay) 
                    (compo ?cpo) (commo ?cmo) (net-name ?nn) (pin-name ?pn)))
  (modify ?ff (grid-y ?y) (came-from south) (can-chng-layer nil))
)

(defrule p360
  (context (present extend-total))
  (total (net-name ?nn) (row-col row) (coor ?y) 
         (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (horizontal-s (net-name ?nn) (min ?min) (max ?max) (com ?lr) (id ?id))
  (last-row ?lr)
  (horizontal (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
              (max ?qw12&:(>= ?qw12 ?max)) (com ?y))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx&:(and (>= ?gx ?min) (<= ?gx ?max))) 
             (grid-y ?gy&:(> ?gy ?y)) (grid-layer ?lay) (pin-name ?pn))
  ?v <- (vertical (net-name nil) (min ?vmin&:(<= ?vmin ?y)) (max ?vmax&:(>= ?vmax ?gy)) 
                  (com ?gx) (layer ?lay) (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qz4&:(<= ?qz4 ?y)) 
                 (max ?qz7&:(>= ?qz7 ?y)) (com ?gx) (layer ?lay)))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw30&:(<= ?qw30 ?gx)) 
                   (max ?qw44&:(>= ?qw44 ?gx)) (com ?y) (layer ?lay)))
  =>
  (assert (vertical (min ?gy) (max ?vmax) (com ?gx) (layer ?lay) (compo ?cpo) 
                    (commo ?cmo) (min-net ?nn) (max-net ?mn)))
  (modify ?v (max ?y) (max-net ?nn))
  (assert (vertical (min ?y) (max ?gy) (com ?gx) (layer ?lay) (compo ?cpo) 
                    (commo ?cmo) (net-name ?nn) (pin-name ?pn)))
  (modify ?ff (grid-y ?y) (came-from north) (can-chng-layer nil))
)

(defrule p361
  (context (present extend-total))
  (total (net-name ?nn) (row-col col) (coor ?x) 
         (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (vertical-s (net-name ?nn) (min ?min) (max ?max) (com 1) (id ?id))
  (vertical (net-name nil) (min ?qw3&:(<= ?qw3 ?min)) 
            (max ?qw12&:(>= ?qw12 ?max)) (com ?x))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx&:(< ?gx ?x)) 
             (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))) 
             (grid-layer ?lay) (pin-name ?pn))
  ?v <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?gx)) 
                    (max ?hmax&:(>= ?hmax ?x)) (com ?gy) (layer ?lay)
                    (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw16&:(<= ?qw16 ?x))
                   (max ?qw48&:(>= ?qw48 ?x)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) 
                 (max ?qw49&:(>= ?qw49 ?gy)) (com ?x) (layer ?lay)))
  =>
  (assert (horizontal (min ?x) (max ?hmax) (com ?gy) (layer ?lay) 
          (compo ?cpo) (commo ?cmo) (min-net ?nn) (max-net ?mn)))
  (modify ?v (max ?gx) (max-net ?nn))
  (assert (horizontal (min ?gx) (max ?x) (com ?gy) (layer ?lay) (compo ?cpo)
          (commo ?cmo) (net-name ?nn) (pin-name ?pn)))
  (modify ?ff (grid-x ?x) (came-from west) (can-chng-layer nil))
)

(defrule p362
  (context (present extend-total))
  (total (net-name ?nn) (row-col col) (coor ?x) 
         (min-xy ?min) (max-xy ?max) (last-xy ?id))
  (vertical-s (net-name ?nn) (min ?min) (max ?max) (com ?lc) (id ?id))
  (last-col ?lc)
  (vertical (net-name nil) (min ?qw3&:(<= ?qw3 ?min))
            (max ?qw12&:(>= ?qw12 ?max)) (com ?x))
  ?ff <- (ff (net-name ?nn) (grid-x ?gx&:(> ?gx ?x)) 
             (grid-y ?gy&:(and (>= ?gy ?min) (<= ?gy ?max))) 
             (grid-layer ?lay) (pin-name ?pn))
  ?v <- (horizontal (net-name nil) (min ?hmin&:(<= ?hmin ?x)) 
                    (max ?hmax&:(>= ?hmax ?gx)) (com ?gy) (layer ?lay)
                    (compo ?cpo) (commo ?cmo) (max-net ?mn))
  (not (horizontal (status nil) (net-name ~?nn&~nil) (min ?qw16&:(<= ?qw16 ?x)) 
                   (max ?qw49&:(>= ?qw49 ?x)) (com ?gy) (layer ?lay)))
  (not (vertical (status nil) (net-name ~?nn&~nil) (min ?qw39&:(<= ?qw39 ?gy)) 
                 (max ?qw50&:(>= ?qw50 ?gy)) (com ?x) (layer ?lay)))
  =>
  (assert (horizontal (min ?gx) (max ?hmax) (com ?gy) (layer ?lay) (compo ?cpo)
                      (commo ?cmo) (min-net ?nn) (max-net ?mn)))
  (modify ?v (max ?x) (max-net ?nn))
  (assert (horizontal (min ?x) (max ?gx) (com ?gy) (layer ?lay) (compo ?cpo) 
                      (commo ?cmo) (net-name ?nn) (pin-name ?pn)))
  (modify ?ff (grid-x ?x) (came-from east) (can-chng-layer nil))
)
