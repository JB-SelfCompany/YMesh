package d.d.meshenger

import java.net.InetAddress
import java.net.InetSocketAddress

// sort addresses by scope (link-local address, hostname, ... , global IP address, DNS domain)
class InetSocketAddressComparator(val lastWorkingAddress: InetSocketAddress? = null) : Comparator<InetSocketAddress> {
    private fun isPrivateAddress(address: InetAddress): Boolean {
        val bytes = address.address
        if (bytes.size == 4) {
            return false // IPv4 addresses are not supported
        } else if (bytes.size == 16) {
            // Only allow 200::/7 and 300::/7
            val firstByte = bytes[0].toUInt() and 0xfeu
            return firstByte != 0x40u && firstByte != 0x60u
        }
        return false
    }

    private fun getPriority(address: InetSocketAddress): Int {
        if (lastWorkingAddress != null && lastWorkingAddress == address) {
            // try last working address first
            return 1
        }

        if (address.isUnresolved) {
            if (address.hostName.endsWith(".lan") || address.hostName.endsWith(".local") || !address.hostName.contains(".")) {
                // local domain or hostname
                return 3
            } else {
                return 6
            }
        } else {
            if (isPrivateAddress(address.address)) {
                return 4
            }
            return 5
        }
    }

    override fun compare(address1: InetSocketAddress, address2: InetSocketAddress): Int {
        val a1 = getPriority(address1)
        val a2 = getPriority(address2)
        if (a1 > a2) {
            return 1
        } else if (a1 < a2) {
            return -1
        } else {
            return 0
        }
    }
}
