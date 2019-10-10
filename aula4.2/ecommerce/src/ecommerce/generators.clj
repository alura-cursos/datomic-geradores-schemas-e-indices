(ns ecommerce.generators
  (:require [clojure.test.check.generators :as gen]))

(defn double-para-bigdecimal [valor]
  (BigDecimal. valor))

(def double-finito (gen/double* {:infinite? false :NaN? false}))
(def bigdecimal (gen/fmap double-para-bigdecimal
                          double-finito))
(def leaf-generators {BigDecimal bigdecimal})