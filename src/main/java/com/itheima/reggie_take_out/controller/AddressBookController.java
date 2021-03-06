package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie_take_out.common.BaseContext;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.AddressBook;
import com.itheima.reggie_take_out.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xushengjie
 * @create 2022/5/4 9:38 PM
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {

	@Autowired
	private AddressBookService addressBookService;

	/**
	 * 新增
	 */
	@PostMapping
	public R<AddressBook> save(@RequestBody AddressBook addressBook) {
		addressBook.setUserId(BaseContext.getCurrentId());
		log.info("addressBook:{}", addressBook);
		addressBookService.save(addressBook);
		return R.success(addressBook);
	}

	/**
	 * 设置默认地址
	 */
	@PutMapping("default")
	public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
		log.info("addressBook:{}", addressBook);
		LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
		wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
		wrapper.set(AddressBook::getIsDefault, false);
		//将当前用户下的所有地址置为非默认地址(默认地址只能有一个)
		addressBookService.update(wrapper);

		//然后将传入的地址设置成默认地址
		addressBook.setIsDefault(true);

		addressBookService.updateById(addressBook);
		return R.success(addressBook);
	}

	/**
	 * 根据id查询地址
	 */
	@GetMapping("/{id}")
	public R get(@PathVariable Long id) {
		AddressBook addressBook = addressBookService.getById(id);
		if (addressBook != null) {
			return R.success(addressBook);
		} else {
			return R.error("没有找到该对象");
		}
	}

	/**
	 * 查询默认地址
	 */
	@GetMapping("default")
	public R<AddressBook> getDefault() {
		LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
		queryWrapper.eq(AddressBook::getIsDefault, 1);

		//SQL:select * from address_book where user_id = ? and is_default = 1
		AddressBook addressBook = addressBookService.getOne(queryWrapper);

		if (null == addressBook) {
			return R.error("没有找到该对象");
		} else {
			return R.success(addressBook);
		}
	}

	/**
	 * 查询指定用户的全部地址
	 */
	@GetMapping("/list")
	public R<List<AddressBook>> list(AddressBook addressBook) {
		addressBook.setUserId(BaseContext.getCurrentId());
		log.info("addressBook:{}", addressBook);

		//条件构造器
		LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
		queryWrapper.orderByDesc(AddressBook::getUpdateTime);

		//SQL:select * from address_book where user_id = ? order by update_time desc
		return R.success(addressBookService.list(queryWrapper));
	}

}
