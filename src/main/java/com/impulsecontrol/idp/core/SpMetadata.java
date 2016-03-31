package com.impulsecontrol.idp.core;

import javax.persistence.*;

/**
 * Created by kerrk on 3/31/16.
 */

@Entity
@Table(name = "sp_metadata")
public class SpMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "acs_url")
    private String acsUrl;

    @Column(name = "audience_restriction")
    private String audienceRestriction;

    @Column(name = "destination_url")
    private String destinationUrl;

    @Column(name = "recipient_url")
    private String recipientUrl;

    @Column(name = "default_relay_state")
    private String defaultRelayState;

    public SpMetadata() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAcsUrl() {
        return acsUrl;
    }

    public void setAcsUrl(String acsUrl) {
        this.acsUrl = acsUrl;
    }

    public String getAudienceRestriction() {
        return audienceRestriction;
    }

    public void setAudienceRestriction(String audienceRestriction) {
        this.audienceRestriction = audienceRestriction;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public String getRecipientUrl() {
        return recipientUrl;
    }

    public void setRecipientUrl(String recipientUrl) {
        this.recipientUrl = recipientUrl;
    }

    public String getDefaultRelayState() {
        return defaultRelayState;
    }

    public void setDefaultRelayState(String defaultRelayState) {
        this.defaultRelayState = defaultRelayState;
    }
}
