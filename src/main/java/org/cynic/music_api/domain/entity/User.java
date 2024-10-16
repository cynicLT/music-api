package org.cynic.music_api.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "\"USER\"")
@DynamicInsert
@DynamicUpdate
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user")
    @SequenceGenerator(name = "user", sequenceName = "USER_SEQ", allocationSize = 0)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FAVORITE_ARTIST_REF_ID")
    private String favoriteArtistRefId;

    public Long getId() {
        return id;
    }

    public String getFavoriteArtistRefId() {
        return favoriteArtistRefId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFavoriteArtistRefId(String favoriteArtistRefId) {
        this.favoriteArtistRefId = favoriteArtistRefId;
    }
}
