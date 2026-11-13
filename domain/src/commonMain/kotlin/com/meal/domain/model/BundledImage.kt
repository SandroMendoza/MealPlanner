package com.meal.domain.model

/**
 * Prefix of a recipe `imageUrl` that names a photo packaged with the app (for example
 * `"seed:chicken_adobo.webp"`) instead of a file on the device. The real location differs per
 * platform and per install, so only the file name is stored and the UI resolves it at load time.
 */
const val BUNDLED_IMAGE_PREFIX = "seed:"
