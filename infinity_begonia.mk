#
# Copyright (C) 2026 Project Infinity-X
#
# SPDX-License-Identifier: Apache-2.0
#

# Release name
PRODUCT_RELEASE_NAME := begonia

# Inherit from those products. Most specific first.
$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/full_base_telephony.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/product_launched_with_p.mk)

# Inherit from begonia device
$(call inherit-product, device/redmi/begonia/device.mk)

# Ship only the device-specific MiuiCameraLeica; skip the generic Aperture camera.
# Must be set before the inherit below, where the conditional is evaluated.
PRODUCT_NO_CAMERA := true

# Inherit common Infinity-X stuff
$(call inherit-product, vendor/infinity/config/common_full_phone.mk)

# Inherit some extras stuff (camera)
$(call inherit-product-if-exists, vendor/MiuiCameraLeica/config.mk)

INFINITY_BUILD_TYPE := UNOFFICIAL
INFINITY_MAINTAINER := krispgece
WITH_GAPPS ?= true

ifeq ($(WITH_GAPPS),true)
DEVICE_PACKAGE_OVERLAYS += device/redmi/begonia/overlay-updater/gapps
else
DEVICE_PACKAGE_OVERLAYS += device/redmi/begonia/overlay-updater/vanilla
endif

# Boot animation
TARGET_BOOT_ANIMATION_RES := 1920

## Device identifier. This must come after all inclusions
PRODUCT_DEVICE := begonia
PRODUCT_NAME := infinity_begonia
PRODUCT_BRAND := Redmi
PRODUCT_MODEL := Redmi Note 8 Pro
PRODUCT_MANUFACTURER := Xiaomi

BUILD_FINGERPRINT := "Redmi/begonia/begonia:11/RP1A.200720.011/V12.5.8.0.RGGMIXM:user/release-keys"

PRODUCT_BUILD_PROP_OVERRIDES += \
    BuildDesc="begonia-user 11 RP1A.200720.011 V12.5.8.0.RGGMIXM release-keys" \
    DeviceName="begonia"

PRODUCT_GMS_CLIENTID_BASE := android-xiaomi
