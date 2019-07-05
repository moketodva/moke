package cn.moke.generator.entity.vo;

import cn.moke.generator.entity.po.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author moke
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVo implements Serializable {

    private static final long serialVersionUID = 5315536611870802514L;

    private String id;

    private String username;

    private List<Role> roles;
}
