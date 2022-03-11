package com.pgoogol.searchservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdressesReadEntity implements BaseEntity {

    //miejscIIPId
    private String id;

    private LocalDateTime cyklZyciaOd;
    private String gmIIPId;
    private String gmIdTeryt;
    private String gmNazwa;
    private String miejscIIPId;
    private String miejscIdTeryt;
    private String miejscNIIPId;
    private String miejscNIdTeryt;
    private String miejscNNazwa;
    private String miejscNRodzaj;


    private String miejscNazwa;
    private String miejscRodzaj;
    private String pktEmuiaIIPId;
    private String pktKodPocztowy;
    private Double pktLat;
    private Double pktLon;
    private String pktNumer;
    private String pktPrgIIPId;
    private String pktStatus;
    private String powIIPId;
    private String powIdTeryt;
    private String powNazwa;
    private String ulIIPId;
    private String ulIdTeryt;
    private String ulNazwaCzesc;
    private String ulNazwaGlowna;
    private String ulNazwaPrzed1;
    private String ulNazwaPrzed2;
    private String ulTyp;
    private String wojIIPId;
    private String wojIdTeryt;
    private String wojNazwa;

}
