package com.mutationmob.jogodavelha;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by rafael on 13/05/16.
 */
    @JsonObject
    public class Menssagem {

        /*
         * Annotate a field that you want sent with the @JsonField marker.
         */
        @JsonField
        public String description;

        /*
         * Note that since this field isn't annotated as a
         * @JsonField, LoganSquare will ignore it when parsing
         * and serializing this class.
         */
        @JsonField
        public int nonJsonField;
    }

