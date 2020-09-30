(ns abeceda.api-test
  (:require [clojure.test :refer :all]
            [abeceda.api :as api]))

(defn scramble-many-impl-fixture
  [f]
  (doall
    (for [impl [api/scramble-freq-set-impl
                api/scramble-coll-loop-impl
                api/scramble-freq-reduce-impl
                api/scramble-group-by-reduce-kv-impl
                api/scramble-fold-impl]]
      (binding [api/*scramble-impl* impl]
        (f)))))

(use-fixtures :each scramble-many-impl-fixture)

(deftest scramble?-with-wrong-arg-type-should-throw-assertion-error-test
  (is
    (thrown?
      AssertionError
      (api/scramble? nil "foo")))
  (is
    (thrown?
      AssertionError
      (api/scramble? "foo" nil))))

(deftest scramble?-with-empty-string-arg-should-throw-assertion-error-test
  (is
    (thrown?
      AssertionError
      (api/scramble? "" "foo")))
  (is
    (thrown?
      AssertionError
      (api/scramble? "foo" ""))))

(deftest scramble?-with-invalid-string-arg-should-throw-assertion-error-test
  (is
    (thrown?
      AssertionError
      (api/scramble? " " "foo")))
  (is
    (thrown?
      AssertionError
      (api/scramble? "foo" " "))))

(deftest scramble?-the-same-string-should-scramble-itself-test
  (is (api/scramble? "a" "a"))
  (is (api/scramble? "foo" "foo")))

(deftest scramble?-example-strings-test
  (is (api/scramble? "rekqodlw" "world"))
  (is (api/scramble? "cedewaraaossoqqyt" "codewars"))
  (is (not (api/scramble? "katas" "steak"))))

(deftest scramble?-letter-frequency-test
  (is (not (api/scramble? "foo" "off")))
  (is (not (api/scramble? "foof" "fooo")))
  (is (not (api/scramble? "trololo" "trolololololo")))
  (is (not (api/scramble? "trololo" "lotlot")))
  (is (api/scramble? "fuuf" "fuf"))
  (is (api/scramble? "trololololololo" "lotlo"))
  (is (api/scramble? "archibaldtuttle" "archibuttle")))

(deftest scramble?-long-string-test
  (let [longstr
        (str "zweojhmrsbvjpkahiadbsswoazhujeydshoqnabkeenhvzndgktygadzyrkqmvdecrwkdxnwqyfkvzwdvfrauuirqijajmotyiras"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "wobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigoqijajmotykmpomkopniunhdiuaisjndoa"
             "oybxtfayovtfaiuxtfoiayxfsmkmklnfyusdvxbsydgfuyvfuyvgfsxyfcyastfcycftecmmdkkpsjydahfdbstbmetxfechemwmi"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "zweojhmrsbvjpkahiadbsswoazhujeydshoqnabkeenhvzndgktygadzyrkqmvdecrwkdxnwqyfkvzwdvfrauuirqijajmotyiras"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "wobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigoqijajmotykmpomkopniunhdiuaisjndoa"
             "oybxtfayovtfaiuxtfoiayxfsmkmklnfyusdvxbsydgfuyvfuyvgfsxyfcyastfcycftecmmdkkpsjydahfdbstbmetxfechemwmi"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "zweojhmrsbvjpkahiadbsswoazhujeydshoqnabkeenhvzndgktygadzyrkqmvdecrwkdxnwqyfkvzwdvfrauuirqijajmotyiras"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "wobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigoqijajmotykmpomkopniunhdiuaisjndoa"
             "oybxtfayovtfaiuxtfoiayxfsmkmklnfyusdvxbsydgfuyvfuyvgfsxyfcyastfcycftecmmdkkpsjydahfdbstbmetxfechemwmi"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "zweojhmrsbvjpkahiadbsswoazhujeydshoqnabkeenhvzndgktygadzyrkqmvdecrwkdxnwqyfkvzwdvfrauuirqijajmotyiras"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb"
             "wobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigoqijajmotykmpomkopniunhdiuaisjndoa"
             "oybxtfayovtfaiuxtfoiayxfsmkmklnfyusdvxbsydgfuyvfuyvgfsxyfcyastfcycftecmmdkkpsjydahfdbstbmetxfechemwmi"
             "xfktftpgsjwvegzklptelxsrqwobmxohbddwofxaddlbsceubjbnxzdgfymdxzdjaydfmvvsvzkwxbvblboczywwpoigovklkozyb")]
    (is (not (api/scramble? longstr (str longstr "foo"))))
    (is (not (api/scramble? longstr (str "bar" longstr))))
    (is (api/scramble? longstr longstr))
    (is (api/scramble? longstr (apply str (reverse longstr))))
    (is (api/scramble? (str longstr "foo") longstr))
    (is (api/scramble? (str "bar" longstr) longstr))))