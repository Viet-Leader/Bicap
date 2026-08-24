1. Khởi động Hardhat Node

Mở terminal:

cd D:\bicap\blockchain
npx hardhat node

Giữ terminal này đang chạy.

Bạn cần thấy account kiểu:

Account #0: 0xf39Fd6e...
(10000 ETH)
2. Deploy Smart Contract
Mở terminal thứ 2:

cd D:\bicap\blockchain

Sau đó chạy script deploy của project bạn.

Ví dụ nếu project đang dùng script deploy hiện tại:

npx hardhat run .\scripts\deploy.ts --network localhost
3. Kiểm tra application.yml

Backend phải trỏ tới node hiện tại
blockchain:
  rpc-url: http://127.0.0.1:8545
  contract-address: 0x5FbDB2315678afecb367f032d93F642f64180aa3
  private-key: 0xac0974bec39a17e36ba4a6b4d238ff944bacb478cbed5efca784d7bf4f2ff80
  4. Khởi động Backend

Mở terminal thứ 3:

cd D:\bicap\backend
.\mvnw spring-boot:run