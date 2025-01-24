package d.d.meshenger

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Settings {
    var username = ""
    var secretKey = byteArrayOf()
    var publicKey = byteArrayOf()
    var nightMode = "auto" // on, off, auto
    var speakerphoneMode = "auto" // on, off, auto
    var blockUnknown = false
    var useNeighborTable = false
    var guessEUI64Address = true
    var promptOutgoingCalls = false
    var videoHardwareAcceleration = true
    var disableCallHistory = false
    var disableProximitySensor = false
    var disableAudioProcessing = false
    var showUsernameAsLogo = true
    var pushToTalk = false
    var startOnBootup = false
    var connectRetries = 3
    var connectTimeout = 2000
    var enableMicrophoneByDefault = true
    var enableCameraByDefault = false
    var selectFrontCameraByDefault = true
    var disableCpuOveruseDetection = false
    var autoAcceptCalls = false
    var menuPassword = ""
    var videoDegradationMode = "balanced"
    var cameraResolution = "auto"
    var cameraFramerate = "auto"
    var automaticStatusUpdates = true
    var themeName = "sky_blue"
    var skipStartupPermissionCheck = false
    var addresses = mutableListOf<String>()

    fun getOwnContact(): Contact {
        return Contact(username, publicKey, addresses)
    }

    fun destroy() {
        publicKey.fill(0)
        secretKey.fill(0)
    }

    companion object {
        fun createDefault(): Settings {
            val s = Settings()
            
            // Set default values
            s.nightMode = "auto"
            s.speakerphoneMode = "auto"
            s.blockUnknown = false
            s.useNeighborTable = false
            s.guessEUI64Address = true
            s.videoHardwareAcceleration = true
            s.disableAudioProcessing = false
            s.connectTimeout = 2000
            s.disableCallHistory = false
            s.disableProximitySensor = false
            s.promptOutgoingCalls = false
            s.showUsernameAsLogo = true
            s.pushToTalk = false
            s.startOnBootup = false
            s.connectRetries = 3
            s.enableMicrophoneByDefault = true
            s.enableCameraByDefault = false
            s.selectFrontCameraByDefault = true
            s.disableCpuOveruseDetection = false
            s.autoAcceptCalls = false
            s.videoDegradationMode = "balanced"
            s.cameraResolution = "auto"
            s.cameraFramerate = "auto"
            s.automaticStatusUpdates = true
            s.themeName = "sky_blue"
            s.skipStartupPermissionCheck = false

            // Get first available IPv6 address with yggdrasil prefix
            val addresses = AddressUtils.collectAddresses()
                .filter { it.address.startsWith("2") && it.address.contains(":") }
                .map { it.address }
            
            if (addresses.isNotEmpty()) {
                s.addresses = mutableListOf(addresses.first())
            } else {
                s.addresses = mutableListOf()
                Log.w("Settings", "No valid IPv6 addresses found")
            }

            return s
        }

        @Throws(JSONException::class)
        fun fromJSON(obj: JSONObject): Settings {
            val s = Settings()
            s.username = obj.getString("username")
            s.secretKey = Utils.hexStringToByteArray(obj.getString("secret_key"))
            s.publicKey = Utils.hexStringToByteArray(obj.getString("public_key"))
            s.nightMode = obj.getString("night_mode")
            s.speakerphoneMode = obj.getString("speakerphone_mode")
            s.blockUnknown = obj.getBoolean("block_unknown")
            s.useNeighborTable = obj.getBoolean("use_neighbor_table")
            s.guessEUI64Address = obj.getBoolean("guess_eui64_address")
            s.videoHardwareAcceleration = obj.getBoolean("video_hardware_acceleration")
            s.disableAudioProcessing = obj.getBoolean("disable_audio_processing")
            s.connectTimeout = obj.getInt("connect_timeout")
            s.disableCallHistory = obj.getBoolean("disable_call_history")
            s.disableProximitySensor = obj.getBoolean("disable_proximity_sensor")
            s.promptOutgoingCalls = obj.getBoolean("prompt_outgoing_calls")
            s.showUsernameAsLogo = obj.getBoolean("show_username_as_logo")
            s.pushToTalk = obj.getBoolean("push_to_talk")
            s.startOnBootup = obj.getBoolean("start_on_bootup")
            s.connectRetries = obj.getInt("connect_retries")
            s.enableMicrophoneByDefault = obj.getBoolean("enable_microphone_by_default")
            s.enableCameraByDefault = obj.getBoolean("enable_camera_by_default")
            s.selectFrontCameraByDefault = obj.getBoolean("select_front_camera_by_default")
            s.disableCpuOveruseDetection = obj.getBoolean("disable_cpu_overuse_detection")
            s.autoAcceptCalls = obj.getBoolean("auto_accept_calls")
            s.menuPassword = obj.getString("menu_password")
            s.videoDegradationMode = obj.getString("video_degradation_mode")
            s.cameraResolution = obj.getString("camera_resolution")
            s.cameraFramerate = obj.getString("camera_framerate")
            s.automaticStatusUpdates = obj.getBoolean("automatic_status_updates")
            s.themeName = obj.getString("theme_name")
            s.skipStartupPermissionCheck = obj.getBoolean("skip_startup_permission_check")

            val array = obj.getJSONArray("addresses")
            val addresses = mutableListOf<String>()
            for (i in 0 until array.length()) {
                var address = array[i].toString()
                if (AddressUtils.isIPAddress(address)) {
                    // Only allow IPv6 addresses with yggdrasil prefix (0x02)
                    if (address.startsWith("2") && address.contains(":")) {
                        address = address.lowercase(Locale.ROOT)
                        if (address !in addresses) {
                            addresses.add(address)
                        }
                    }
                } else if (AddressUtils.isDomain(address)) {
                    address = address.lowercase(Locale.ROOT)
                    if (address !in addresses) {
                        addresses.add(address)
                    }
                } else {
                    Log.d("Settings", "invalid address $address")
                    continue
                }
            }
            s.addresses = addresses.toMutableList()

            return s
        }

        fun toJSON(s: Settings): JSONObject {
            val obj = JSONObject()
            obj.put("username", s.username)
            obj.put("secret_key", Utils.byteArrayToHexString(s.secretKey))
            obj.put("public_key", Utils.byteArrayToHexString(s.publicKey))
            obj.put("night_mode", s.nightMode)
            obj.put("speakerphone_mode", s.speakerphoneMode)
            obj.put("block_unknown", s.blockUnknown)
            obj.put("use_neighbor_table", s.useNeighborTable)
            obj.put("guess_eui64_address", s.guessEUI64Address)
            obj.put("video_hardware_acceleration", s.videoHardwareAcceleration)
            obj.put("disable_audio_processing", s.disableAudioProcessing)
            obj.put("connect_timeout", s.connectTimeout)
            obj.put("disable_call_history", s.disableCallHistory)
            obj.put("disable_proximity_sensor", s.disableProximitySensor)
            obj.put("prompt_outgoing_calls", s.promptOutgoingCalls)
            obj.put("show_username_as_logo", s.showUsernameAsLogo)
            obj.put("push_to_talk", s.pushToTalk)
            obj.put("start_on_bootup", s.startOnBootup)
            obj.put("connect_retries", s.connectRetries)
            obj.put("enable_microphone_by_default", s.enableMicrophoneByDefault)
            obj.put("enable_camera_by_default", s.enableCameraByDefault)
            obj.put("select_front_camera_by_default", s.selectFrontCameraByDefault)
            obj.put("disable_cpu_overuse_detection", s.disableCpuOveruseDetection)
            obj.put("auto_accept_calls", s.autoAcceptCalls)
            obj.put("menu_password", s.menuPassword)
            obj.put("video_degradation_mode", s.videoDegradationMode)
            obj.put("camera_resolution", s.cameraResolution)
            obj.put("camera_framerate", s.cameraFramerate)
            obj.put("automatic_status_updates", s.automaticStatusUpdates)
            obj.put("theme_name", s.themeName)
            obj.put("skip_startup_permission_check", s.skipStartupPermissionCheck)

            val addresses = JSONArray()
            for (address in s.addresses) {
                addresses.put(address)
            }
            obj.put("addresses", addresses)

            return obj
        }
    }
}
