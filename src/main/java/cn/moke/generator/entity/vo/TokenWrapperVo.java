package cn.moke.generator.entity.vo;

import cn.moke.generator.entity.po.Role;
import cn.moke.generator.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * @author moke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenWrapperVo implements Serializable {

    private static final long serialVersionUID = 5315536611870802514L;

    private String token;

    private User user;
}
