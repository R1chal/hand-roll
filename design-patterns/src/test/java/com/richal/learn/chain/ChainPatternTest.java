package com.richal.learn.chain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 责任链模式测试类
 * 演示责任链模式的使用
 *
 * @author Richal
 */
public class ChainPatternTest {

    private ApprovalChain standardChain;

    @Before
    public void setUp() {
        standardChain = ApprovalChain.buildStandardChain();
    }

    @Test
    public void testSupervisorApproval() {
        System.out.println("=== 测试主管审批（1天） ===");

        LeaveRequest request = new LeaveRequest("张三", 1, "家里有事");
        String result = standardChain.process(request);

        assertTrue(result.contains("主管"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testManagerApproval() {
        System.out.println("=== 测试经理审批（3天） ===");

        LeaveRequest request = new LeaveRequest("李四", 3, "回家探亲");
        String result = standardChain.process(request);

        assertTrue(result.contains("经理"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testDirectorApproval() {
        System.out.println("=== 测试总监审批（7天） ===");

        LeaveRequest request = new LeaveRequest("王五", 7, "出国旅游");
        String result = standardChain.process(request);

        assertTrue(result.contains("总监"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testCEOApproval() {
        System.out.println("=== 测试CEO审批（15天） ===");

        LeaveRequest request = new LeaveRequest("赵六", 15, "结婚度蜜月");
        String result = standardChain.process(request);

        assertTrue(result.contains("CEO"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testCEOReject() {
        System.out.println("=== 测试CEO拒绝（35天） ===");

        LeaveRequest request = new LeaveRequest("孙七", 35, "环游世界");
        String result = standardChain.process(request);

        assertTrue(result.contains("CEO"));
        assertTrue(result.contains("拒绝"));
    }

    @Test
    public void testChainPropagation() {
        System.out.println("=== 测试责任链传递（2天，主管->经理） ===");

        LeaveRequest request = new LeaveRequest("周八", 2, "看病");
        String result = standardChain.process(request);

        // 主管无权处理，应该传递给经理
        assertTrue(result.contains("经理"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testCustomChain() {
        System.out.println("=== 测试自定义责任链 ===");

        // 只有经理和CEO的责任链
        Approver manager = new Manager("李经理");
        Approver ceo = new CEO("王CEO");

        ApprovalChain customChain = ApprovalChain.buildCustomChain(manager, ceo);
        customChain.printChain();

        LeaveRequest request = new LeaveRequest("员工A", 5, "家庭原因");
        String result = customChain.process(request);

        // 经理无权处理5天，应该传递给CEO
        assertTrue(result.contains("CEO"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testSingleApprover() {
        System.out.println("=== 测试单一审批者 ===");

        Approver ceo = new CEO("独裁CEO");
        ApprovalChain singleChain = ApprovalChain.buildCustomChain(ceo);

        LeaveRequest request = new LeaveRequest("员工B", 10, "休假");
        String result = singleChain.process(request);

        assertTrue(result.contains("CEO"));
        assertTrue(result.contains("批准"));
    }

    @Test
    public void testNoNextApprover() {
        System.out.println("=== 测试没有下级审批者 ===");

        // 只有主管，没有上级
        Approver supervisor = new Supervisor("孤独主管");
        ApprovalChain shortChain = ApprovalChain.buildCustomChain(supervisor);

        LeaveRequest request = new LeaveRequest("员工C", 5, "旅游");
        String result = shortChain.process(request);

        // 主管无权处理5天，且没有上级
        assertTrue(result.contains("无权处理"));
        assertTrue(result.contains("没有上级"));
    }

    @Test
    public void testPrintChain() {
        System.out.println("=== 测试打印责任链结构 ===");

        standardChain.printChain();

        Approver first = standardChain.getFirstApprover();
        assertNotNull(first);
        assertTrue(first instanceof Supervisor);
    }

    @Test
    public void testMultipleRequests() {
        System.out.println("=== 测试处理多个请求 ===");

        LeaveRequest[] requests = {
            new LeaveRequest("员工1", 1, "看病"),
            new LeaveRequest("员工2", 3, "家事"),
            new LeaveRequest("员工3", 7, "旅游"),
            new LeaveRequest("员工4", 15, "结婚")
        };

        for (LeaveRequest request : requests) {
            String result = standardChain.process(request);
            assertNotNull(result);
            assertTrue(result.contains("批准") || result.contains("拒绝"));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyChain() {
        System.out.println("=== 测试空责任链 ===");

        // 应该抛出异常
        ApprovalChain.buildCustomChain();
    }

    /**
     * 主方法，演示责任链模式的完整使用场景
     */
    public static void main(String[] args) {
        System.out.println("========== 责任链模式演示 ==========\n");

        // 构建标准审批责任链
        ApprovalChain chain = ApprovalChain.buildStandardChain();

        // 打印责任链结构
        chain.printChain();

        // 场景1：1天请假 - 主管审批
        System.out.println("【场景1】员工请假1天");
        LeaveRequest request1 = new LeaveRequest("张三", 1, "身体不适，需要看病");
        chain.process(request1);

        // 场景2：3天请假 - 经理审批
        System.out.println("【场景2】员工请假3天");
        LeaveRequest request2 = new LeaveRequest("李四", 3, "回老家处理家事");
        chain.process(request2);

        // 场景3：7天请假 - 总监审批
        System.out.println("【场景3】员工请假7天");
        LeaveRequest request3 = new LeaveRequest("王五", 7, "国庆假期出游");
        chain.process(request3);

        // 场景4：15天请假 - CEO审批
        System.out.println("【场景4】员工请假15天");
        LeaveRequest request4 = new LeaveRequest("赵六", 15, "结婚度蜜月");
        chain.process(request4);

        // 场景5：30天请假 - CEO审批
        System.out.println("【场景5】员工请假30天");
        LeaveRequest request5 = new LeaveRequest("孙七", 30, "出国深造");
        chain.process(request5);

        // 场景6：35天请假 - CEO拒绝
        System.out.println("【场景6】员工请假35天");
        LeaveRequest request6 = new LeaveRequest("周八", 35, "环游世界");
        chain.process(request6);

        // 场景7：自定义责任链
        System.out.println("\n【场景7】自定义责任链（只有经理和CEO）");
        Approver manager = new Manager("自定义经理");
        Approver ceo = new CEO("自定义CEO");
        ApprovalChain customChain = ApprovalChain.buildCustomChain(manager, ceo);
        customChain.printChain();

        LeaveRequest request7 = new LeaveRequest("员工A", 5, "家庭紧急情况");
        customChain.process(request7);

        System.out.println("\n========== 演示结束 ==========");

        // 总结
        System.out.println("\n【责任链模式优点】");
        System.out.println("1. 降低耦合度：请求发送者和接收者解耦");
        System.out.println("2. 简化对象：对象不需要知道链的结构");
        System.out.println("3. 增强灵活性：可以动态地增加或删除处理者");
        System.out.println("4. 增强职责分配：每个处理者专注于自己的职责");

        System.out.println("\n【适用场景】");
        System.out.println("1. 有多个对象可以处理同一个请求");
        System.out.println("2. 在不明确指定接收者的情况下，向多个对象中的一个提交请求");
        System.out.println("3. 可动态指定一组对象处理请求");
    }
}
