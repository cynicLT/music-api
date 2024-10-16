package org.cynic.music_api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class Constants {

    public static final String PRE_AUTHORIZE_EXPRESSION = "isFullyAuthenticated()";
    public static final Marker AUDIT_MARKER = MarkerFactory.getMarker("AUDIT");
    public static final String ARTISTS_SPACE_NAME = "artists";
    public static final String ARTIST_TOP_ALBUMS_SPACE_NAME = "albums";
    public static final String PROXY_SPACE_NAME = "proxy";
    public static final String PROXY_FALLBACK = "fallback";
    public static final Integer DEFAULT_LIMIT = 200;
    public static final Integer DEFAULT_TOP_LIMIT = 5;

    private Constants() {
    }

    public enum ItunesJsonFieldPointer {
        RESULT("/results"),
        WRAPPER("/wrapperType"),
        ALBUM_REF_ID("/collectionId"),
        ALBUM_TITLE("/collectionName"),
        ARTIST_REF_ID("/amgArtistId"),
        ARTIST_TITLE("/artistName");

        private final String fieldName;

        ItunesJsonFieldPointer(String fieldName) {
            this.fieldName = fieldName;
        }

        public String fieldName() {
            return fieldName;
        }
    }

    public enum WrapperType {
        ARTIST("artist"),
        ALBUM("collection");

        private final String propertyValue;

        WrapperType(String propertyValue) {
            this.propertyValue = propertyValue;
        }

        public String propertyValue() {
            return propertyValue;
        }
    }
}
