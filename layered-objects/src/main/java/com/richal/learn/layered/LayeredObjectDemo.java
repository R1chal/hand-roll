package com.richal.learn.layered;

import com.richal.learn.layered.dto.CreateOrderRequestDTO;
import com.richal.learn.layered.dto.UserOrderDTO;
import com.richal.learn.layered.service.UserOrderService;
import com.richal.learn.layered.vo.UserDetailVO;

import java.math.BigDecimal;

/**
 * 分层对象演示程序
 *
 * 演示 VO/BO/PO/DTO/DO 在实际业务中的流转
 *
 * @author Richal
 * @since 2026/03/21
 */
public class LayeredObjectDemo {

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("     VO/BO/PO/DTO/DO 分层对象示例演示");
        System.out.println("============================================\n");

        UserOrderService service = new UserOrderService();

        // ========== 演示 1：获取用户详情（PO -> DO -> VO）==========
        System.out.println("【演示 1】获取用户详情");
        System.out.println("数据流转：PO -> DO -> VO");
        System.out.println("-".repeat(50));

        UserDetailVO userDetail = service.getUserDetail(1L);
        System.out.println("返回给前端的 VO 对象：");
        System.out.println("  用户ID: " + userDetail.getUserId());
        System.out.println("  用户名: " + userDetail.getUsername());
        System.out.println("  手机号(脱敏): " + userDetail.getMaskedPhone());
        System.out.println("  邮箱(脱敏): " + userDetail.getMaskedEmail());
        System.out.println("  余额: " + userDetail.getFormattedBalance());
        System.out.println("  状态: " + userDetail.getStatusDesc());
        System.out.println("  注册时间: " + userDetail.getRegisterTimeStr());
        System.out.println("  订单统计: ");
        System.out.println("    - 总订单数: " + userDetail.getOrderStatistics().getTotalCount());
        System.out.println("    - 待支付: " + userDetail.getOrderStatistics().getPendingPayCount());
        System.out.println("    - 已完成: " + userDetail.getOrderStatistics().getCompletedCount());
        System.out.println("    - 累计消费: " + userDetail.getOrderStatistics().getTotalAmountStr());
        System.out.println();

        // ========== 演示 2：创建订单（DTO -> DO -> BO -> PO）==========
        System.out.println("【演示 2】创建订单");
        System.out.println("数据流转：DTO -> DO -> BO -> PO");
        System.out.println("-".repeat(50));

        CreateOrderRequestDTO requestDTO = new CreateOrderRequestDTO();
        requestDTO.setUserId(1L);
        requestDTO.setProductId(100L);
        requestDTO.setProductName("蓝牙耳机");
        requestDTO.setUnitPrice(new BigDecimal("99.00"));
        requestDTO.setQuantity(2);
        requestDTO.setAddress("北京市朝阳区xxx街道");
        requestDTO.setContactPhone("13800138000");
        requestDTO.setRemark("请尽快发货");

        System.out.println("前端传入的 DTO：");
        System.out.println("  " + requestDTO);
        System.out.println();

        boolean success = service.createOrder(requestDTO);
        System.out.println("订单创建结果: " + (success ? "成功" : "失败"));
        System.out.println();

        // ========== 演示 3：获取用户订单数据（PO -> DO -> DTO）==========
        System.out.println("【演示 3】获取用户订单数据（用于远程调用）");
        System.out.println("数据流转：PO -> DO -> DTO");
        System.out.println("-".repeat(50));

        UserOrderDTO userOrderDTO = service.getUserOrderData(1L);
        System.out.println("返回给远程调用的 DTO 对象：");
        System.out.println("  用户ID: " + userOrderDTO.getUserId());
        System.out.println("  用户名: " + userOrderDTO.getUsername());
        System.out.println("  手机号: " + userOrderDTO.getPhone());
        System.out.println("  订单总数: " + userOrderDTO.getTotalOrderCount());
        System.out.println("  订单总金额: ¥" + userOrderDTO.getTotalOrderAmount());
        System.out.println("  传输时间: " + userOrderDTO.getTransferTime());
        System.out.println("  订单列表:");
        for (UserOrderDTO.OrderSummaryDTO order : userOrderDTO.getOrders()) {
            System.out.println("    - " + order.getOrderNo() + ": ¥" + order.getAmount()
                    + " (" + order.getStatusDesc() + ")");
        }
        System.out.println();

        // ========== 总结 ==========
        System.out.println("============================================");
        System.out.println("                 总结");
        System.out.println("============================================");
        System.out.println();
        System.out.println("PO (Persistent Object) - 持久化对象");
        System.out.println("  用途: 与数据库表结构一一对应");
        System.out.println("  特点: 包含所有数据库字段，如 password、status 等");
        System.out.println();
        System.out.println("DO (Domain Object) - 领域对象");
        System.out.println("  用途: 业务领域的核心对象");
        System.out.println("  特点: 包含业务方法，如 pay()、ship()、complete()");
        System.out.println("       使用枚举类型，如 OrderStatus");
        System.out.println();
        System.out.println("DTO (Data Transfer Object) - 数据传输对象");
        System.out.println("  用途: 层与层之间的数据传输");
        System.out.println("  特点: 可以聚合多个对象，如 UserOrderDTO 包含用户+订单");
        System.out.println("       用于远程调用，减少调用次数");
        System.out.println();
        System.out.println("VO (View Object) - 视图对象");
        System.out.println("  用途: 返回给前端展示");
        System.out.println("  特点: 敏感字段脱敏，如 maskedPhone、maskedEmail");
        System.out.println("       数据格式化，如 formattedBalance、statusDesc");
        System.out.println();
        System.out.println("BO (Business Object) - 业务对象");
        System.out.println("  用途: 封装复杂业务逻辑");
        System.out.println("  特点: 协调多个 DO，如 OrderProcessBO 处理下单流程");
        System.out.println("       包含业务规则，如会员等级计算、折扣计算");
    }
}
