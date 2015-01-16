(deftemplate unit
   (slot unit-name)
   (slot file-name))

(deftemplate pin
   (slot net-name)
   (slot pin-name)
   (slot external-net-name)
   (slot external-pin-name)
   (slot pin-left-x)
   (slot pin-left-y)
   (slot fixed-pin)
   (slot pin-x)
   (slot pin-y)
   (slot pin-layer) 
   (slot pin-layer-constraint)
   (slot pin-channel-side)
   (slot pin-is-attached))

(deftemplate channel
   (slot channel-bottom-left-x)
   (slot channel-bottom-left-y)
   (slot channel-top-right-x)
   (slot channel-top-right-y)
   (slot channel-width)
   (slot channel-length) 
   (slot no-of-left-pins)
   (slot no-of-right-pins)
   (slot no-of-bottom-pins)
   (slot no-of-top-pins)
   (slot right-fixed)
   (slot left-fixed)
   (slot bottom-fixed)
   (slot top-fixed)
   (slot channel-type)
   (slot no-of-fixed-sides))

(deftemplate net
   (slot net-name)
   (slot parent-name)
   (slot net-no-of-pins)
   (slot net-left-x)
   (slot net-left-y)
   (slot net-right-x)
   (slot net-right-y)
   (slot no-of-left-pins)
   (slot no-of-right-pins)
   (slot no-of-top-pins)
   (slot no-of-bottom-pins)
   (slot net-is-routed)
   (slot left-most-pin-name)
   (slot right-most-pin-name)
   (slot bottom-most-pin-name)
   (slot top-most-pin-name)
   (slot fixed-net)
   (slot net-layer)
   (slot external-net-name)
   (slot no-of-inter)
   (slot max-no-of-via))

(deftemplate layer-info
   (slot layer-name)
   (slot layer-order)
   (slot layer-priority))

(deftemplate context
   (slot present)
   (slot previous)
   (slot saved))

(deftemplate ff
   (slot net-name)
   (slot pin-name)
   (slot grid-layer)
   (slot grid-x)
   (slot grid-y)
   (slot came-from)
   (slot can-chng-layer))

(deftemplate next-net-to-be-routed
   (slot net-name)
   (slot no-of-attached-pins)
   (slot criteria))

(deftemplate to-be-routed
   (slot net-name)
   (slot no-of-attached-pins))

(deftemplate occupied
   (slot x)
   (slot y)
   (slot m))

(deftemplate constraint
   (slot constraint-type)
   (slot constraint-relation)
   (slot channel-side)
   (slot constraint-reason)
   (slot net-name-1)
   (slot net-name-2)
   (slot pin-name-1)
   (slot pin-name-2)
   (slot seg-id-1)
   (slot seg-id-2))

(deftemplate congestion 
   (slot direction)
   (slot coordinate)
   (slot no-of-nets)
   (slot extra-nets)
   (slot como))

(deftemplate intersection 
   (slot net-name)
   (slot direction)
   (slot min)
   (slot max))

(deftemplate horizontal 
   (slot min)
   (slot max)
   (slot com)
   (slot compo)
   (slot commo)
   (slot layer)
   (slot status)
   (slot min-net)
   (slot max-net)
   (slot net-name)
   (slot pin-name))

(deftemplate vertical 
   (slot min)
   (slot max)
   (slot com)
   (slot compo)
   (slot commo)
   (slot layer)
   (slot status)
   (slot min-net)
   (slot max-net)
   (slot net-name)
   (slot pin-name))

(deftemplate horizontal-s 
   (slot net-name)
   (slot min)
   (slot max)
   (slot com)
   (slot id)
   (slot top-count)
   (slot bot-count)
   (slot sum)
   (slot difference)
   (slot absolute)
   (slot side))

(deftemplate vertical-s 
   (slot net-name)
   (slot min)
   (slot max)
   (slot com)
   (slot id)
   (slot top-count)
   (slot bot-count)
   (slot sum)
   (slot difference)
   (slot absolute)
   (slot side))

(deftemplate total 
   (slot net-name)
   (slot row-col)
   (slot coor)
   (slot level-pins)
   (slot total-pins)
   (slot cong)
   (slot min-xy)
   (slot max-xy)
   (slot last-pin)
   (slot last-xy)
   (multislot nets))

(deftemplate tree 
   (slot net-name)
   (slot orientation)
   (slot com)
   (slot min)
   (slot max)
   (slot count)
   (slot id))
