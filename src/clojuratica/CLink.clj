; ***** BEGIN LICENSE BLOCK *****
; Version: MPL 1.1/GPL 2.0/LGPL 2.1
;
; The contents of this file are subject to the Mozilla Public License Version
; 1.1 (the "License"); you may not use this file except in compliance with
; the License. You may obtain a copy of the License at
; http://www.mozilla.org/MPL/
;
; Software distributed under the License is distributed on an "AS IS" basis,
; WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
; for the specific language governing rights and limitations under the
; License.
;
; The Original Code is the Clojure-Mathematica interface library Clojuratica.
;
; The Initial Developer of the Original Code is Garth Sheldon-Coulson.
; Portions created by the Initial Developer are Copyright (C) 2009
; the Initial Developer. All Rights Reserved.
;
; Contributor(s):
;
; Alternatively, the contents of this file may be used under the terms of
; either the GNU General Public License Version 2 or later (the "GPL"), or
; the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
; in which case the provisions of the GPL or the LGPL are applicable instead
; of those above. If you wish to allow use of your version of this file only
; under the terms of either the GPL or the LGPL, and not to allow others to
; use your version of this file under the terms of the MPL, indicate your
; decision by deleting the provisions above and replace them with the notice
; and other provisions required by the GPL or the LGPL. If you do not delete
; the provisions above, a recipient may use your version of this file under
; the terms of any one of the MPL, the GPL or the LGPL.
;
; ***** END LICENSE BLOCK *****

(ns clojuratica.CLink
    (:gen-class
     :methods [                 [convert [com.wolfram.jlink.Expr] Object]
                                [parse [Object] com.wolfram.jlink.Expr]
               #^{:static true} [mirrorClasspath [] Object]]
     :state state
     :init init
     :constructors {[] []})
    (:import [com.wolfram.jlink StdLink Expr MathLinkFactory]
             [clojure.lang.*]
             [java.io StringReader])
    (:use [clojuratica.clojuratica]
          [clojuratica.low-level]))

(defn -convert [this expr]
  (let [parse (:parse (.state this))]
    (parse expr)))

(defn -parse [this obj]
  (.getExpr (convert obj)))

(defn -mirrorClasspath []
  (let [classloader (.getClassLoader (StdLink/getLink))
        m-classpath (seq (.getClassPath classloader))
        m-url-classpath (for [path m-classpath] (str "file://" path))
        c-classpath (seq (.getURLs (clojure.lang.RT/baseLoader)))]
    (doseq [url m-url-classpath]
      (when-not (some #{url} c-classpath)
        (add-classpath url)))))

(defn -init []
  (let [kernel-link (StdLink/getLink)]
    ;(.discardAnswer kernel-link)
    (let [;parallel (if false :parallel :serial)
          evaluate (get-evaluator kernel-link)
          mmafn (if true (get-mmafn evaluate) nil)
          parse (get-parser kernel-link mmafn)]
      [[] {:evaluate evaluate :mmafn mmafn :parse parse}])))
