package com.richal.learn.layered.mapper;

import com.richal.learn.layered.domain.OrderDO;
import com.richal.learn.layered.domain.UserDO;
import com.richal.learn.layered.entity.OrderPO;
import com.richal.learn.layered.entity.UserPO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 对象转换器
 *
 * 负责 PO、DO、DTO、VO 之间的转换
 * 在实际项目中，可以使用 MapStruct 等工具自动生成
 *
 * @author Richal
 * @since 2026/03/21
 */
public class ObjectConverter {

    // ========== PO <-> DO 转换 ==========

    /**
     * UserPO 转 UserDO
     *
     * @param po 持久化对象
     * @return 领域对象
     */
    public static UserDO toUserDO(UserPO po) {
        if (po == null) {
            return null;
        }
        UserDO do_ = new UserDO();
        do_.setId(po.getId());
        do_.setUsername(po.getUsername());
        do_.setPhone(po.getPhone());
        do_.setEmail(po.getEmail());
        do_.setRegisterTime(po.getCreateTime());

        // 状态转换
        if (po.getStatus() != null) {
            do_.setStatus(UserDO.UserStatus.of(po.getStatus()));
        }

        return do_;
    }

    /**
     * UserDO 转 UserPO
     *
     * @param do_ 领域对象
     * @return 持久化对象
     */
    public static UserPO toUserPO(UserDO do_) {
        if (do_ == null) {
            return null;
        }
        UserPO po = new UserPO();
        po.setId(do_.getId());
        po.setUsername(do_.getUsername());
        po.setPhone(do_.getPhone());
        po.setEmail(do_.getEmail());

        // 状态转换
        if (do_.getStatus() != null) {
            po.setStatus(do_.getStatus().getCode());
        }

        return po;
    }

    /**
     * OrderPO 转 OrderDO
     *
     * @param po 持久化对象
     * @return 领域对象
     */
    public static OrderDO toOrderDO(OrderPO po) {
        if (po == null) {
            return null;
        }
        OrderDO do_ = new OrderDO();
        do_.setId(po.getId());
        do_.setOrderNo(po.getOrderNo());
        do_.setAmount(new BigDecimal(po.getAmount()).divide(new BigDecimal("100")));
        do_.setCreateTime(po.getCreateTime());

        // 状态转换
        if (po.getStatus() != null) {
            do_.setStatus(OrderDO.OrderStatus.values()[po.getStatus()]);
        }

        return do_;
    }

    /**
     * OrderDO 转 OrderPO
     *
     * @param do_ 领域对象
     * @return 持久化对象
     */
    public static OrderPO toOrderPO(OrderDO do_) {
        if (do_ == null) {
            return null;
        }
        OrderPO po = new OrderPO();
        po.setId(do_.getId());
        po.setOrderNo(do_.getOrderNo());

        // 金额转换：元 -> 分
        if (do_.getAmount() != null) {
            po.setAmount(do_.getAmount().multiply(new BigDecimal("100")).longValue());
        }

        // 用户ID
        if (do_.getUser() != null) {
            po.setUserId(do_.getUser().getId());
        }

        // 状态转换
        if (do_.getStatus() != null) {
            po.setStatus(do_.getStatus().ordinal());
        }

        po.setCreateTime(do_.getCreateTime());

        return po;
    }

    // ========== List 转换 ==========

    /**
     * UserPO List 转 UserDO List
     *
     * @param poList 持久化对象列表
     * @return 领域对象列表
     */
    public static List<UserDO> toUserDOList(List<UserPO> poList) {
        List<UserDO> result = new ArrayList<>();
        if (poList != null) {
            for (UserPO po : poList) {
                result.add(toUserDO(po));
            }
        }
        return result;
    }

    /**
     * OrderPO List 转 OrderDO List
     *
     * @param poList 持久化对象列表
     * @return 领域对象列表
     */
    public static List<OrderDO> toOrderDOList(List<OrderPO> poList) {
        List<OrderDO> result = new ArrayList<>();
        if (poList != null) {
            for (OrderPO po : poList) {
                result.add(toOrderDO(po));
            }
        }
        return result;
    }
}
