(ns ecommerce.aula1
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.config :as db.config]
            [ecommerce.db.produto :as db.produto]
            [schema.core :as s]
            [ecommerce.db.venda :as db.venda]
            [schema-generators.generators :as g]
            [ecommerce.model :as model]
            [clojure.test.check.generators :as gen]))

(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(pprint (db.produto/todos (d/db conn)))

(defn double-para-bigdecimal [valor]
  (BigDecimal. "15"))

(def bigdecimal-generator (gen/fmap double-para-bigdecimal
                                    gen/double))
(def meus-geradores {BigDecimal bigdecimal-generator})
(pprint (g/sample 100 model/Categoria))
(pprint (g/sample 100 model/Variacao meus-geradores))





