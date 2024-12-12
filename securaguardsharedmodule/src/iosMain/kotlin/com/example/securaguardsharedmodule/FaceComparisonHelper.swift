import Foundation
import LocalAuthentication

class FaceComparisonHelper {
    static func compareAndEncrypt(
        capturedData: Data,
        storedData: Data,
        imei: String
    ) -> Data? {
        // Compare the images
        guard compareImages(capturedData: capturedData, storedData: storedData) else {
            print("Face comparison failed.")
            return nil
        }

        // Prepare the result
        let resultString = "Yes:\(imei)"
        guard let resultData = resultString.data(using: .utf8) else {
            print("Failed to convert result to data.")
            return nil
        }

        // Encrypt the result using Secure Enclave
        return encryptUsingPrivateKey(resultData)
    }

    private static func compareImages(capturedData: Data, storedData: Data) -> Bool {
        // Basic data size comparison (you can add ML-based comparison here)
        return capturedData == storedData
    }

    private static func encryptUsingPrivateKey(_ data: Data) -> Data? {
        let tag = "com.example.securaguard.encryptionkey".data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecReturnRef as String: true
        ]

        var keyRef: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &keyRef)
        guard status == errSecSuccess, let privateKey = keyRef as? SecKey else {
            print("Failed to retrieve private key.")
            return nil
        }

        let algorithm: SecKeyAlgorithm = .rsaEncryptionOAEPSHA256

        guard SecKeyIsAlgorithmSupported(privateKey, .encrypt, algorithm) else {
            print("Algorithm not supported.")
            return nil
        }

        var error: Unmanaged<CFError>?
        guard let encryptedData = SecKeyCreateEncryptedData(
            privateKey,
            algorithm,
            data as CFData,
            &error
        ) else {
            print("Encryption failed: \(error?.takeRetainedValue() ?? CFError() as! Error)")
            return nil
        }

        return encryptedData as Data
    }
}
