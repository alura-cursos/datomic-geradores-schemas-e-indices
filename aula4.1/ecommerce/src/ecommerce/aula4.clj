(ns ecommerce.aula4
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db.config :as db.config]
            [ecommerce.db.produto :as db.produto]
            [schema.core :as s]
            [ecommerce.db.venda :as db.venda]
            [schema-generators.generators :as g]
            [ecommerce.model :as model]
            [ecommerce.generators :as generators]))

(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(defn propriedades-do-valor [valor]
  (cond (= valor java.util.UUID) {:db/valueType :db.type/uuid
                                  :db/unique    :db.unique/identity}
        (= valor s/Str) {:db/valueType :db.type/string}
        (= valor BigDecimal) {:db/valueType :db.type/bigdec}
        :else {:db/valueType (str "desconhecido: " valor)}))

(defn chave-valor-para-definicao [[chave valor]]
  (let [base {:db/ident       chave
              :db/cardinality :db.cardinality/one}
        extra (propriedades-do-valor valor)
        schema-do-datomic (merge base extra)]
    schema-do-datomic))

(defn schema-to-datomic [definicao]
  (mapv chave-valor-para-definicao definicao))

(pprint (schema-to-datomic model/Categoria))
(pprint (schema-to-datomic model/Variacao))















