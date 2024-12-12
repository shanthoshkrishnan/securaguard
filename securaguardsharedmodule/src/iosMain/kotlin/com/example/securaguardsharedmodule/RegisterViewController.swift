class RegisterViewController: UIViewController {
    private let nameTextField = UITextField()
    private let addressTextField = UITextField()
    private let registerButton = UIButton()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    private func setupUI() {
        view.backgroundColor = .white

        nameTextField.placeholder = "Enter Name"
        nameTextField.borderStyle = .roundedRect

        addressTextField.placeholder = "Enter Address"
        addressTextField.borderStyle = .roundedRect

        registerButton.setTitle("Register", for: .normal)
        registerButton.setTitleColor(.white, for: .normal)
        registerButton.backgroundColor = .green
        registerButton.addTarget(self, action: #selector(register), for: .touchUpInside)

        let stack = UIStackView(arrangedSubviews: [nameTextField, addressTextField, registerButton])
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

    @objc private func register() {
        // Save user details and navigate to Capture Page
        let captureVC = CaptureViewController()
        navigationController?.pushViewController(captureVC, animated: true)
    }
}
