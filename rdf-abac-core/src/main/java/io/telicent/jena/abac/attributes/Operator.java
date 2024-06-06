/*
 *  Copyright (c) Telicent Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.telicent.jena.abac.attributes;

import java.util.Objects;

public enum Operator {
    LT("<"), LE("<=") , EQ("="), NE("!="), GE(">="), GT(">") ;

    private final String str;

    Operator(String form) {
        this.str = form;
    }

    public String str() { return str; }

    public static Operator of(String str) {
        Objects.requireNonNull(str);
        return switch(str) {
            case "<"  -> LT;
            case "<=" -> LE;
            case "=",
                 "==" -> EQ;
            case "!=" -> NE;
            case ">=" -> GE;
            case ">"  -> GT;
            default   ->
                throw new IllegalArgumentException("Argument: '"+str+"'");
        };
    }

    @Override
    public String toString() {
        return name()+"('"+str+"')";
    }
}
