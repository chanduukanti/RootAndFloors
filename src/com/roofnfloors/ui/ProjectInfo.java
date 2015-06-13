package com.roofnfloors.ui;

public class ProjectInfo {
    public String addressLine1;
    public String addressLine2;
    public String brochure;
    public String city;
    public String description;
    public Documents[] documents;
    public boolean hidePrice;
    public String landmark;
    public String listingId;
    public String listingName;
    public String locality;
    public String maxArea;
    public String minArea;
    public String maxPrice;
    public String minPrice;
    public String maxPricePerSqft;
    public String minPricePerSqft;
    public String noOfAvailableUnits;
    public String noOfBlocks;
    public String noOfUnits;
    public String otherInfo;
    public String packageId;
    public String posessionDate;
    public String projectType;
    public String status;
    public Summary summary[];
    public String url;
    public String[] videoLinks;
    public String amenities;
    public String approvalNumber;
    public String approvedBy;
    public String bankApprovals;
    public String builderCredaiStatus;
    public String builderDescription;
    public String builderId;
    public String builderLogo;
    public String builderName;
    public String builderUrl;
    public String electricityConnection;
    public String lastMileLandmark;
    public String lastMileLat;
    public String lastMileLon;
    public String propertyTypes;
    public String lat;
    public String lon;
    public String otherAmenities;
    public String otherBanks;
    public String specification;
    public String waterTypes;
    public String ImageUrls;

    public class Summary {
        String area;
        String bathrooms;
        String bedroom;
        String carParking;
        String[] floorPlans;
        String landArea;
        String noOfUnits;
        String price;
        String propertyType;
    }

    public class Documents {
        String directionFacing;
        boolean primary;
        String reference;
        String text;
        String type;

        @Override
        public String toString() {
            return reference;
        }
    }

    public ProjectInfo(String addressLine1, String addressLine2, String city,
            String description, String listingId, String maxArea,
            String minArea, String maxPricePerSqft, String minPricePerSqft,
            String noOfAvailableUnits, String noOfBlocks, String posessionDate,
            String projectType, String status, String amenities,
            String builderDescription, String builderName, String builderUrl,
            String otherAmenities, String specification, String firstImageUrl) {

        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.amenities = amenities;
        this.builderDescription = builderDescription;
        this.builderName = builderName;
        this.builderUrl = builderUrl;
        this.city = city;
        this.description = description;
        this.listingId = listingId;
        this.maxArea = maxArea;

        this.maxPricePerSqft = maxPricePerSqft;
        this.minArea = minArea;

        this.minPricePerSqft = minPricePerSqft;
        this.noOfAvailableUnits = noOfAvailableUnits;
        this.noOfBlocks = noOfBlocks;
        this.otherAmenities = otherAmenities;
        this.posessionDate = posessionDate;
        this.projectType = projectType;
        this.specification = specification;
        this.status = status;
        this.ImageUrls = firstImageUrl;
    }

    public ProjectInfo() {
    }
}
