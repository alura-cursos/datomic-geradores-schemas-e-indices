(ns ecommerce.db.categoria
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.model :as model]
            [ecommerce.db.entidade :as db.entidade]
            [schema.core :as s]))

(defn db-adds-de-atribuicao-de-categorias [produtos categoria]
  (reduce (fn [db-adds produto] (conj db-adds [:db/add
                                               [:produto/id (:produto/id produto)]
                                               :produto/categoria
                                               [:categoria/id (:categoria/id categoria)]]))
          []
          produtos))

(defn atribui! [conn produtos categoria]
  (let [a-transacionar (db-adds-de-atribuicao-de-categorias produtos categoria)]
    (d/transact conn a-transacionar)))


; como esses dois estão genéricos poderiam ser um só
; mas vamos manter dois pois se você usa schema fica mais fácil de trabalhar
(s/defn adiciona! [conn, categorias :- [model/Categoria]]
  (d/transact conn categorias))

(s/defn todas :- [model/Categoria] [db]
  (db.entidade/datomic-para-entidade
    (d/q '[:find [(pull ?categoria [*]) ...]
           :where [?categoria :categoria/id]]
         db)))
