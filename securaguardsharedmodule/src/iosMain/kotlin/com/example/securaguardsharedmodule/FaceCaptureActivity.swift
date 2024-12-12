import UIKit
import AVFoundation
import Security

class FaceCaptureActivity: UIViewController, AVCapturePhotoCaptureDelegate {
    private var captureSession: AVCaptureSession!
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var photoOutput: AVCapturePhotoOutput!
    private let captureButton = UIButton()
    private var publicKey: SecKey?

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black

        // Initialize camera and UI
        setupCamera()
        setupUI()

        // Generate or retrieve RSA keys for Secure Enclave
        publicKey = setupSecureEnclaveKeys()
    }

    // MARK: - Camera Setup
    private func setupCamera() {
        captureSession = AVCaptureSession()

        // Configure camera input
        guard let camera = AVCaptureDevice.default(for: .video) else {
            showErrorAlert(message: "No camera available on this device.")
            return
        }

        do {
            let videoInput = try AVCaptureDeviceInput(device: camera)
            if captureSession.canAddInput(videoInput) {
                captureSession.addInput(videoInput)
            } else {
                showErrorAlert(message: "Unable to add video input to the session.")
                return
            }
        } catch {
            showErrorAlert(message: "Error accessing camera: \(error.localizedDescription)")
            return
        }

        // Configure photo output
        photoOutput = AVCapturePhotoOutput()
        if captureSession.canAddOutput(photoOutput) {
            captureSession.addOutput(photoOutput)
        } else {
            showErrorAlert(message: "Unable to configure photo output.")
            return
        }

        // Configure preview layer
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.frame = view.bounds
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.addSublayer(previewLayer)

        captureSession.startRunning()
    }

    // MARK: - UI Setup
    private func setupUI() {
        // Configure capture button
        captureButton.setTitle("Capture", for: .normal)
        captureButton.backgroundColor = .red
        captureButton.layer.cornerRadius = 25
        captureButton.translatesAutoresizingMaskIntoConstraints = false
        captureButton.addTarget(self, action: #selector(capturePhoto), for: .touchUpInside)
        view.addSubview(captureButton)

        // Button layout constraints
        NSLayoutConstraint.activate([
            captureButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            captureButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -20),
            captureButton.widthAnchor.constraint(equalToConstant: 120),
            captureButton.heightAnchor.constraint(equalToConstant: 50)
        ])
    }

    // MARK: - Secure Enclave Key Management
    private func setupSecureEnclaveKeys() -> SecKey? {
        let tag = "com.example.securaguard.encryptionkey".data(using: .utf8)!

        // Check if the key already exists
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecAttrApplicationTag as String: tag,
            kSecReturnRef as String: true
        ]

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        if status == errSecSuccess, let existingKey = item as? SecKey {
            return existingKey
        }

        // Generate a new key pair if not already present
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
            kSecAttrKeySizeInBits as String: 2048,
            kSecAttrTokenID as String: kSecAttrTokenIDSecureEnclave,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: true,
                kSecAttrApplicationTag as String: tag
            ]
        ]

        var error: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
            showErrorAlert(message: "Key generation error: \(error!.takeRetainedValue().localizedDescription)")
            return nil
        }

        return SecKeyCopyPublicKey(privateKey)
    }

    // MARK: - Capture Photo
    @objc private func capturePhoto() {
        let settings = AVCapturePhotoSettings()
        photoOutput.capturePhoto(with: settings, delegate: self)
    }

    func photoOutput(_ output: AVCapturePhotoOutput, didFinishProcessingPhoto photo: AVCapturePhoto, error: Error?) {
        if let error = error {
            showErrorAlert(message: "Error capturing photo: \(error.localizedDescription)")
            return
        }

        guard let photoData = photo.fileDataRepresentation() else {
            showErrorAlert(message: "Unable to process photo data.")
            return
        }

        // Encrypt and handle the captured photo
        if let encryptedData = encryptPhotoData(data: photoData) {
            handleEncryptedPhoto(encryptedData)
        } else {
            showErrorAlert(message: "Failed to encrypt photo data.")
        }
        if isSecondCapture {
                // Call FaceComparisonHelper for comparison and encryption
                let result = FaceComparisonHelper.compareAndEncrypt(
                    capturedData: photoData,
                    storedData: fetchStoredImageFromTEE(),
                    imei: getDeviceIMEI()
                )
                if let result = result {
                    print("Comparison and encryption succeeded. Sending result to UIDAI.")
                    sendToUIDAI(result: result)
                } else {
                    print("Comparison or encryption failed.")
                }
            } else {
                // First-time capture logic (storing in TEE)
                if let encryptedData = encryptPhotoData(data: photoData) {
                    print("Photo successfully encrypted and stored.")
                    storeInTEE(encryptedData)
                } else {
                    print("Failed to encrypt photo data.")
                }
            }
        }
    }

    // MARK: - Encryption
    private func encryptPhotoData(data: Data) -> Data? {
        guard let publicKey = publicKey else {
            showErrorAlert(message: "Public key not available for encryption.")
            return nil
        }

        let algorithm: SecKeyAlgorithm = .rsaEncryptionOAEPSHA256

        // Ensure the encryption algorithm is supported
        guard SecKeyIsAlgorithmSupported(publicKey, .encrypt, algorithm) else {
            showErrorAlert(message: "Encryption algorithm not supported.")
            return nil
        }

        var error: Unmanaged<CFError>?
        guard let encryptedData = SecKeyCreateEncryptedData(
            publicKey,
            algorithm,
            data as CFData,
            &error
        ) else {
            showErrorAlert(message: "Encryption failed: \(error!.takeRetainedValue().localizedDescription)")
            return nil
        }

        return encryptedData as Data
    }

    // MARK: - Post-Processing
    private func handleEncryptedPhoto(_ encryptedData: Data) {
        // Placeholder for further actions with the encrypted data
        // Example: Save to file, send to backend, etc.
        print("Encrypted photo data size: \(encryptedData.count) bytes")
    }

    // MARK: - Error Handling
    private func showErrorAlert(message: String) {
        let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "OK", style: .default))
        DispatchQueue.main.async {
            self.present(alert, animated: true)
        }
    }


