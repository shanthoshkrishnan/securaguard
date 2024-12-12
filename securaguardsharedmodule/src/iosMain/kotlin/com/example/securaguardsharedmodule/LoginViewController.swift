import UIKit

class LoginViewController: UIViewController {
    private let aadhaarTextField = UITextField()
    private let mobileTextField = UITextField()
    private let otpTextField = UITextField()
    private let otpButton = UIButton()
    private let loginButton = UIButton()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    private func setupUI() {
        view.backgroundColor = .white

        aadhaarTextField.placeholder = "Enter Aadhaar Number"
        aadhaarTextField.keyboardType = .numberPad
        aadhaarTextField.borderStyle = .roundedRect

        mobileTextField.placeholder = "Enter Mobile Number"
        mobileTextField.keyboardType = .numberPad
        mobileTextField.borderStyle = .roundedRect

        otpTextField.placeholder = "Enter OTP"
        otpTextField.keyboardType = .numberPad
        otpTextField.borderStyle = .roundedRect

        otpButton.setTitle("Send OTP", for: .normal)
        otpButton.setTitleColor(.blue, for: .normal)
        otpButton.addTarget(self, action: #selector(sendOTP), for: .touchUpInside)

        loginButton.setTitle("Login", for: .normal)
        loginButton.setTitleColor(.white, for: .normal)
        loginButton.backgroundColor = .blue
        loginButton.addTarget(self, action: #selector(validateLogin), for: .touchUpInside)

        let stack = UIStackView(arrangedSubviews: [aadhaarTextField, mobileTextField, otpButton, otpTextField, loginButton])
        stack.axis = .vertical
        stack.spacing = 16
        stack.translatesAutoresizingMaskIntoConstraints = false

        view.addSubview(stack)

        NSLayoutConstraint.activate([
            stack.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            stack.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            stack.widthAnchor.constraint(equalToConstant: 300)
        ])
    }

    @objc private func sendOTP() {
        // Simulate sending OTP
        let mobile = mobileTextField.text ?? ""
        print("OTP sent to \(mobile)")
    }

    @objc private func validateLogin() {
        // Validate OTP and Aadhaar number
        guard let otp = otpTextField.text, otp == "123456" else {
            print("Invalid OTP")
            return
        }
        // Navigate to Register Page
        let registerVC = RegisterViewController()
        navigationController?.pushViewController(registerVC, animated: true)
    }
}
